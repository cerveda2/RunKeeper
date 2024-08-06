package cz.dcervenka.run.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import cz.dcervenka.core.domain.run.RemoteRunDataSource
import cz.dcervenka.core.domain.run.Run
import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.EmptyResult
import cz.dcervenka.core.domain.util.Result
import kotlinx.coroutines.tasks.await

class FirebaseRunDataSource : RemoteRunDataSource {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val currentUserEmail: String?
        get() = FirebaseAuth.getInstance().currentUser?.email

    override suspend fun getRuns(): Result<List<Run>, DataError.Network> {
        val userEmail = currentUserEmail ?: return Result.Error(DataError.Network.UNAUTHORIZED)

        return try {
            val runsSnapshot = firestore.collection("users")
                .document(userEmail)
                .collection("runs")
                .get()
                .await()
            val runs = runsSnapshot.documents.mapNotNull { it.toObject<RunDto>()?.toRun() }
            Result.Success(runs)
        } catch (e: Exception) {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    override suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> {
        val userEmail = currentUserEmail ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        var runDto = run.toRunDto()

        return try {
            // Upload the image to Firebase Storage under the user's folder
            val imageRef = storage.reference.child("runs/$userEmail/${runDto.id}.jpg")
            imageRef.putBytes(mapPicture).await()

            // Get the download URL
            val mapPictureUrl = imageRef.downloadUrl.await().toString()

            // Update runDto with the image URL
            runDto = runDto.copy(mapPictureUrl = mapPictureUrl)

            // Save the updated RunDto to the user's runs subcollection in Firestore
            firestore.collection("users")
                .document(userEmail)
                .collection("runs")
                .document(runDto.id)
                .set(runDto)
                .await()

            // Return the Run object with the URL
            Result.Success(runDto.toRun())
        } catch (e: Exception) {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    override suspend fun deleteRun(id: String): EmptyResult<DataError.Network> {
        val userEmail = currentUserEmail ?: return Result.Error(DataError.Network.UNAUTHORIZED)

        return try {
            // Delete the document from the user's runs subcollection in Firestore
            firestore.collection("users")
                .document(userEmail)
                .collection("runs")
                .document(id)
                .delete()
                .await()

            // Delete the associated image from Firebase Storage
            val imageRef = storage.reference.child("runs/$userEmail/$id.jpg")
            imageRef.delete().await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }
}
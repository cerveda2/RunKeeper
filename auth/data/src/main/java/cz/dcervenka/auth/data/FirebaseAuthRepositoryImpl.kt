package cz.dcervenka.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import cz.dcervenka.auth.domain.AuthRepository
import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.EmptyResult
import cz.dcervenka.core.domain.util.Result
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return try {
            val task = auth.createUserWithEmailAndPassword(email, password)
            task.await()
            if (task.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(DataError.Network.SERVER_ERROR)
            }
        } catch (e: FirebaseAuthException) {
            Result.Error(DataError.Network.UNAUTHORIZED)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        return try {
            val task = auth.signInWithEmailAndPassword(email, password)
            task.await()
            if (task.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(DataError.Network.SERVER_ERROR)
            }
        } catch (e: FirebaseAuthException) {
            Result.Error(DataError.Network.UNAUTHORIZED)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}
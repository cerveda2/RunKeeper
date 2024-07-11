package cz.dcervenka.auth.data

import cz.dcervenka.auth.domain.AuthRepository
import cz.dcervenka.core.data.networking.post
import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
) : AuthRepository {

    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password,
            )
        )
    }
}
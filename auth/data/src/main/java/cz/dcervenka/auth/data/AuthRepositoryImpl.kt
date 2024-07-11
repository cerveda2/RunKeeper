package cz.dcervenka.auth.data

import cz.dcervenka.auth.domain.AuthRepository
import cz.dcervenka.core.data.networking.post
import cz.dcervenka.core.domain.AuthInfo
import cz.dcervenka.core.domain.SessionStorage
import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.EmptyResult
import cz.dcervenka.core.domain.util.Result
import cz.dcervenka.core.domain.util.asEmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage,
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

    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = "/login",
            body = LoginRequest(
                email = email,
                password = password,
            )
        )
        if (result is Result.Success) {
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )
        }
        return result.asEmptyResult()
    }
}
package cz.dcervenka.auth.domain

import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}
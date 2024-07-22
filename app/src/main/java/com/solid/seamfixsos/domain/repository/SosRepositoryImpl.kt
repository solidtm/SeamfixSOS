package com.solid.seamfixsos.domain.repository

import com.solid.seamfixsos.data.model.Dto
import com.solid.seamfixsos.data.remote.source.SosDataSource
import com.solid.seamfixsos.domain.functional.ErrorHelper.handleException
import com.solid.seamfixsos.domain.mapper.map
import com.solid.seamfixsos.domain.model.Domain
import com.solid.seamfixsos.domain.functional.Result
import kotlinx.coroutines.flow.*

class SosRepositoryImpl(
    private val source: SosDataSource,
) : SosRepository {

    override suspend fun createSos(request: Dto.SosRequest): Flow<Result<Domain.SosResponse>> {
        return flow {
            emit(Result.Loading())

            val sos = try {
                source.createSos(request)
            }catch (e: Exception) {
                emit(Result.Error(message = handleException(e)))
                return@flow
            }
            emit(Result.Success(sos.map()))
        }
    }
}

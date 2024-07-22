package com.solid.seamfixsos.domain.repository

import com.solid.seamfixsos.data.model.Dto
import com.solid.seamfixsos.domain.functional.Result
import com.solid.seamfixsos.domain.model.Domain
import kotlinx.coroutines.flow.Flow

interface SosRepository {
    suspend fun createSos(request: Dto.SosRequest) : Flow<Result<Domain.SosResponse>>
}
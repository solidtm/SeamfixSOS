package com.solid.seamfixsos.data.remote.source

import com.solid.seamfixsos.data.model.Dto
import com.solid.seamfixsos.data.remote.api.SosService


class SosDataSourceImpl(
    private val service: SosService
): SosDataSource {
    override suspend fun createSos(request: Dto.SosRequest): Dto.SosResponse  = service.createSos(request)
}
package com.solid.seamfixsos.data.remote.source

import com.solid.seamfixsos.data.model.Dto


interface SosDataSource {
    suspend fun createSos(request: Dto.SosRequest) : Dto.SosResponse
}
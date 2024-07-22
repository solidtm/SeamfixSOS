package com.solid.seamfixsos.data.remote.api

import com.solid.seamfixsos.data.model.Dto
import retrofit2.http.Body
import retrofit2.http.POST

interface SosService {
    @POST(HttpRoutes.CREATE_SOS)
    suspend fun createSos(@Body request: Dto.SosRequest): Dto.SosResponse
}
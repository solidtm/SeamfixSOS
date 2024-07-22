package com.solid.seamfixsos.domain.mapper

import com.solid.seamfixsos.data.model.Dto
import com.solid.seamfixsos.domain.model.Domain

fun Dto.SosResponse.map(): Domain.SosResponse {
    return Domain.SosResponse(
        message = message,
        status = status
    )
}

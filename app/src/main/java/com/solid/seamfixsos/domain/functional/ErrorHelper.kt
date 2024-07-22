package com.solid.seamfixsos.domain.functional

import com.solid.seamfixsos.domain.model.Domain
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

object ErrorHelper {
    fun handleException(exception: Throwable): String {
        val message = when (exception) {
            is TimeoutException -> "Connection timeout. Please try again"
            is ConnectException -> "Couldn't connect. Please check your internet"
            is SocketTimeoutException -> "Connection timeout. Please check your internet connection"
            is UnknownHostException -> "Couldn't connect to server. Please check your internet connection"
            is HttpException -> { extractMessage(exception.response()?.errorBody()?.string())
            }
            else -> "An error occurred. Please try again"
        }
        return if (message.length > 100) "Server returned an error. Please try again" else message
    }

    private fun extractMessage(json: String?): String {
        val message = "Server returned an error. Please try again"
        if (json == null) return message
        return try {
            val response = json.toObject<Domain.GenericResponse>()
            response.message ?: message
        } catch (exception: Exception) {
            exception.printStackTrace()
            message
        }
    }

    private inline fun <reified T> typeToken(): Type {
        return Types.newParameterizedType(T::class.java)
    }

    private inline fun <reified T> String.toObject(): T {
        val type = typeToken<T>()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter<T>(type)
        return adapter.fromJson(this) ?: throw IllegalArgumentException("Cannot parse JSON to object")
    }
}
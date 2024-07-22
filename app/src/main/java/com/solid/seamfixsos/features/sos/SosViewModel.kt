package com.solid.seamfixsos.features.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solid.seamfixsos.data.model.Dto
import com.solid.seamfixsos.data.remote.api.Result
import com.solid.seamfixsos.domain.model.Domain
import com.solid.seamfixsos.domain.repository.SosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SosViewModel(
    private val repository: SosRepository
): ViewModel() {

    private val _sos = MutableStateFlow<Result<Domain.SosResponse>>(Result.Loading())
    val sosResponse = _sos.asStateFlow()

    fun createSos(request: Dto.SosRequest){
        viewModelScope.launch {
            repository.createSos(request).collectLatest { result ->
                when(result) {
                    is Result.Error -> {
                        _sos.update { Result.Error(message = result.message ?: "An error occurred, please try again.") }
                    }
                    is Result.Success -> {
                        _sos.update { result }
                    }
                    is Result.Loading -> {_sos.update { Result.Loading() }}
                }
            }
        }
    }
}
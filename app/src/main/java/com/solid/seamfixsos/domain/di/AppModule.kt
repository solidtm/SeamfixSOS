package com.solid.seamfixsos.domain.di

import com.solid.seamfixsos.BuildConfig
import com.solid.seamfixsos.data.remote.api.SosService
import com.solid.seamfixsos.data.remote.api.SosServiceFactory
import com.solid.seamfixsos.data.remote.source.SosDataSource
import com.solid.seamfixsos.data.remote.source.SosDataSourceImpl
import com.solid.seamfixsos.domain.repository.SosRepository
import com.solid.seamfixsos.domain.repository.SosRepositoryImpl
import com.solid.seamfixsos.features.sos.SosViewModel
import com.solid.seamfixsos.features.util.LocationHelper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SosService> {
        SosServiceFactory.createApiService(BuildConfig.DEBUG)
    }
    single<SosDataSource>{
        SosDataSourceImpl(get())
    }
    single<SosRepository>{
        SosRepositoryImpl(get())
    }
    single {
        LocationHelper(get())
    }
    viewModel{
        SosViewModel(get())
    }
}
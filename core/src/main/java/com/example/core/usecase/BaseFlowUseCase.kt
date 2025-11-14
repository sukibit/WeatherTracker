package com.example.core.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseFlowUseCase<T, Params> {
    protected abstract fun execute(params: Params): Flow<T>
    operator fun invoke(params: Params? = null): Flow<Result<T>> = flow {
        try {
            execute(params ?: Unit as Params).collect { data ->
                emit(Result.success(data))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
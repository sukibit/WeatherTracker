package com.example.feature.weather.domain

abstract class BaseSuspendUseCase<out T, in Params> {
    protected abstract suspend fun execute(params: Params): T
    suspend operator fun invoke(params: Params? = null): Result<T> {
        return try {
            Result.success(execute(params ?: Unit as Params))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package edu.nuce.apps.newflowtypes

import retrofit2.HttpException

sealed class Result<out R> {
    data class Success<out T>(val data: T): Result<T>()
    object Loading : Result<Nothing>()
    data class Error(val error: Exception): Result<Nothing>()
}

val <T> Result<T>.errorCode: Int?
    get() = ((this as? Result.Error)?.error as? HttpException)?.code()

package edu.nuce.apps.newflowtypes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@ExperimentalCoroutinesApi
class MainActivityViewModel : ViewModel() {

    private val userId = MutableStateFlow(0)

    // cold streams stateIn(Flow -> StateFlow) shareIn(Flow -> SharedFlow) (hot streams)
    // latest 1 -> getUsers(1) higher order flow
    // 2 -> getUsers(2)
    val result: StateFlow<Result<Int>> = userId.transformLatest { newUserId ->
        emit(getUsers(newUserId))
    }.onEach {
        _errorMessage.tryOffer(NetworkErrorCodeConverter.convert(it.errorCode).toString())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading
    )

    private val _errorMessage = Channel<String>(1, BufferOverflow.DROP_LATEST)
    val errorMessage: SharedFlow<String> =
        _errorMessage.receiveAsFlow().shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000))

    //    val isLoading: StateFlow<Boolean> = result.map {
//        it == Result.Loading
//    }.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5000),
//        false
//    )
//
//    val uiData: StateFlow<Int?> = result.map {
//        (it as? Result.Success)?.data
//    }.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5000),
//        null
//    )
//
    private suspend fun getUsers(userId: Int): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
//                val response = apiService.get....(userId)
                Result.Success(userId)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
//
//    private fun getUsers2(userId: Int): Flow<Result<Int>> {
//        return flowOf(1, 2, 3)
//            .map { Result.Success(it) }
//            .catch { e -> Result.Error(Exception(e)) }
//            .flowOn(Dispatchers.IO)
//    }
}
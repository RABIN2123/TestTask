package com.example.testtask.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testtask.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class GifListViewModel(private val repository: MainRepository) : ViewModel() {
    private val _listCurrentState = MutableStateFlow(GifList())
    private val _listNextState = MutableStateFlow(GifList())
    private val _listPrevState = MutableStateFlow(GifList())
    private val _uiState = MutableStateFlow(UiState())
    val listState: StateFlow<GifList> = _listCurrentState
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _listCurrentState.value = repository.getGifs(OFFSET_START)
                _listNextState.value = repository.getGifs(OFFSET_START + 50)
            } catch (ex: Exception) {
                Log.d("Error", "Init error: $ex")
                uiStateErrorStatus(ex)
            }
        }
        uiStateResponseUpdate()
        uiStateButtonUpdate()
    }

    private fun uiStateErrorStatus(ex: Exception) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { ui ->
                when (ex) {
                    is UnknownHostException -> ui.copy(error = UiState.ErrorStatus.FAIL_CONNECT)
                    else -> ui.copy(error = UiState.ErrorStatus.OTHER)
                }
            }
            _uiState.update { ui ->
                ui.copy(prevPage = false, nextPage = false)
            }
        }
    }

    private fun uiStateResponseUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            _listCurrentState.collect { item ->
                _uiState.update { ui ->
                    when (item.status) {
                        UiState.StatusResponse.OK.num -> ui.copy(response = UiState.StatusResponse.OK)
                        UiState.StatusResponse.UNAUTHORIZED.num -> ui.copy(response = UiState.StatusResponse.UNAUTHORIZED)
                        UiState.StatusResponse.FORBIDDEN.num -> ui.copy(response = UiState.StatusResponse.FORBIDDEN)
                        UiState.StatusResponse.TOOMANYREQUESTS.num -> ui.copy(response = UiState.StatusResponse.TOOMANYREQUESTS)
                        else -> ui.copy()
                    }
                }
            }
        }
    }

    private fun uiStateButtonUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            async {
                _listPrevState.collect { item ->
                    _uiState.update { ui ->
                        if (item.list.isEmpty()) {
                            ui.copy(prevPage = false)
                        } else {
                            ui.copy(prevPage = true)
                        }
                    }
                }
            }
            async {
                _listNextState.collect { item ->
                    _uiState.update { ui ->
                        if (item.list.isEmpty()) {
                            ui.copy(nextPage = false)
                        } else {
                            ui.copy(nextPage = true)
                        }
                    }
                }
            }
        }
    }


    fun getGifPrevPage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _listNextState.value = _listCurrentState.value
                _listCurrentState.value = _listPrevState.value
                _listPrevState.value =
                    repository.getGifs(_listPrevState.value.currentOffset - OFFSET_PAGE)
            } catch (ex: Exception) {
                Log.d("Error", "PrevPage: $ex")
                uiStateErrorStatus(ex)
            }
        }
    }

    fun getGifNextPage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _listPrevState.value = _listCurrentState.value
                _listCurrentState.value = _listNextState.value
                _listNextState.value =
                    repository.getGifs(_listNextState.value.currentOffset + OFFSET_PAGE)

            } catch (ex: Exception) {
                Log.d("Error", "NextPage: $ex")
                uiStateErrorStatus(ex)
            }
        }
    }

    companion object {
        private const val OFFSET_START = 0
        private const val OFFSET_PAGE = 50
        fun provideFactory(
            repository: MainRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GifListViewModel(repository) as T
            }
        }
    }
}
package com.aldisyarif.magicreader.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldisyarif.magicreader.enums.RequestStatus
import com.aldisyarif.magicreader.usecase.DeleteTextUseCase
import com.aldisyarif.magicreader.usecase.UpdateTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTextViewModel @Inject constructor(
    private val updateTextUseCase: UpdateTextUseCase,
    private val deleteTextUseCase: DeleteTextUseCase
): ViewModel() {

    private val _successUpdateText = MutableStateFlow<Boolean?>(null)
    val successUpdateText get() = _successUpdateText.asStateFlow()

    private val _failedUpdateText = MutableStateFlow<String?>(null)
    val failedUpdateText get() = _failedUpdateText.asStateFlow()

    private val _loadingUpdateText = MutableStateFlow<Boolean?>(null)
    val loadingUpdateText get() = _loadingUpdateText.asStateFlow()


    private val _successDeleteText = MutableStateFlow<Boolean?>(null)
    val successDeleteText get() = _successDeleteText.asStateFlow()

    private val _failedDeleteText = MutableStateFlow<String?>(null)
    val failedDeleteText get() = _failedDeleteText.asStateFlow()

    private val _loadingDeleteText = MutableStateFlow<Boolean?>(null)
    val loadingDeleteText get() = _loadingDeleteText.asStateFlow()

    fun updateText(key: String, text: String){
        viewModelScope.launch {
            updateTextUseCase(key, text).collect {
                when(it.requestStatus){
                    RequestStatus.LOADING -> {
                        _loadingUpdateText.emit(true)
                    }
                    RequestStatus.SUCCESS -> {
                        _loadingUpdateText.emit(false)
                        val result = it.data
                        result?.addOnSuccessListener {
                            _successUpdateText.value = true
                        }?.addOnFailureListener {
                            _failedUpdateText.value = it.localizedMessage
                        }
                    }
                    RequestStatus.ERROR -> {
                        _loadingUpdateText.emit(false)
                        _failedUpdateText.emit(it.message)
                    }
                }
            }
        }
    }

    fun deleteText(key: String){
        viewModelScope.launch {
            deleteTextUseCase(key).collect {
                when(it.requestStatus){
                    RequestStatus.LOADING -> {
                        _loadingDeleteText.emit(true)
                    }
                    RequestStatus.SUCCESS -> {
                        _loadingDeleteText.emit(false)
                        val result = it.data
                        result?.addOnSuccessListener {
                            _successDeleteText.value = true
                        }?.addOnFailureListener {
                            _failedDeleteText.value = it.localizedMessage
                        }
                    }
                    RequestStatus.ERROR -> {
                        _loadingDeleteText.emit(false)
                    }
                }
            }
        }
    }
}
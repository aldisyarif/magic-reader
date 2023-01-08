package com.aldisyarif.magicreader.ui.home

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldisyarif.magicreader.enums.RequestStatus
import com.aldisyarif.magicreader.usecase.PostTextUseCase
import com.aldisyarif.magicreader.usecase.ProcessImageUseCase
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val processImageUseCase: ProcessImageUseCase,
    private val postTextUseCase: PostTextUseCase
): ViewModel() {

    private val _successProcess = MutableStateFlow<Text?>(null)
    val successProcess get() = _successProcess.asStateFlow()

    private val _failedProcess = MutableStateFlow<String?>(null)
    val failedProcess get() = _failedProcess.asStateFlow()

    private val _loadingProcess = MutableStateFlow<Boolean?>(null)
    val loadingProcess get() = _loadingProcess.asStateFlow()

    private val _successSaveText = MutableStateFlow<String?>(null)
    val successSaveText get() = _successSaveText.asStateFlow()

    private val _failedSaveText = MutableStateFlow<String?>(null)
    val failedSaveText get() = _failedSaveText.asStateFlow()

    private val _loadingSaveText = MutableStateFlow<Boolean?>(null)
    val loadingSaveText get() = _loadingSaveText.asStateFlow()

    fun processImage(bitmap: Bitmap){
        viewModelScope.launch {
            processImageUseCase(bitmap).collect {
                when(it.requestStatus){
                    RequestStatus.LOADING -> {
                        _loadingProcess.emit(true)
                    }
                    RequestStatus.SUCCESS -> {
                        _loadingProcess.emit(false)
                        val result = it.data
                        result?.addOnSuccessListener {
                            _successProcess.value = it
                            Log.d("MainActivity", "onCreate: ${it.text}")
                        }?.addOnFailureListener {
                                _failedProcess.value = it.localizedMessage
                                Log.d("MainActivity", "onCreate: ${it.localizedMessage}")
                        }
                    }
                    RequestStatus.ERROR -> {
                        _loadingProcess.emit(false)
                        _failedProcess.emit(it.message)
                    }
                }
            }
        }
    }

    fun saveTextInDB(text: String){
        viewModelScope.launch {
            postTextUseCase(text).collect {
                when(it.requestStatus){
                    RequestStatus.LOADING -> {
                        _loadingSaveText.emit(true)
                    }
                    RequestStatus.SUCCESS -> {
                        val textId = it.data?.textId
                        val result = it.data?.task
                        result?.addOnSuccessListener {
                            _loadingSaveText.value = false
                            _successSaveText.value = textId
                        }?.addOnFailureListener {
                            _failedSaveText.value = it.localizedMessage
                        }
                    }
                    RequestStatus.ERROR -> {
                        _loadingSaveText.emit(false)
                        _failedSaveText.emit(it.message)
                    }

                }
            }
        }
    }

}
package com.aldisyarif.magicreader.ui.texts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldisyarif.magicreader.data.model.TextImage
import com.aldisyarif.magicreader.usecase.GetListTextNoteUseCase
import com.aldisyarif.magicreader.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextsViewModel @Inject constructor(
    private val getListTextNoteUseCase: GetListTextNoteUseCase
): ViewModel() {

    private val _textsNoteResponse = MutableStateFlow<Resources<MutableList<TextImage>>>(Resources.loading())
    val textsNoteResponse get() = _textsNoteResponse.asStateFlow()

    fun getListNoteText(){
        viewModelScope.launch {
            getListTextNoteUseCase().collect {
                when {
                    it.isSuccess -> {
                        _textsNoteResponse.emit(Resources.success(it.getOrNull()))
                    }
                    it.isFailure -> {
                        val errorMsg = it.exceptionOrNull()?.localizedMessage ?: "Internal server error"
                        _textsNoteResponse.emit(Resources.error(errorMsg, null))
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

}
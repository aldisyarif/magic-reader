package com.aldisyarif.magicreader.usecase

import android.graphics.Bitmap
import com.aldisyarif.magicreader.utils.Resources
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProcessImageUseCase {
    operator fun invoke(bitmap: Bitmap): Flow<Resources<Task<Text>>> {
        return flow {
            emit(Resources.loading())
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val result = recognizer.process(image)
                emit(Resources.success(result))
            } catch (e: Exception){
                emit(Resources.error(e.localizedMessage ?: "Unknown Error", null))
            }
        }.flowOn(Dispatchers.IO)
    }

}
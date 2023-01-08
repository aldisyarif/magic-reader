package com.aldisyarif.magicreader.usecase

import com.aldisyarif.magicreader.repository.IMagicReaderRepository
import com.aldisyarif.magicreader.utils.Resources
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UpdateTextUseCase(
    private val repository: IMagicReaderRepository
) {
    operator fun invoke(key: String, text: String): Flow<Resources<Task<Void>>> {
        return flow {
            emit(Resources.loading())
            try {
                val response = repository.updateTextImageByKey(key, text)
                emit(Resources.success(response))
            } catch (e: Exception) {
                emit(Resources.error(e.localizedMessage ?: "Unknown Error", null))
            }
        }.flowOn(Dispatchers.IO)
    }
}
package com.aldisyarif.magicreader.usecase

import com.aldisyarif.magicreader.repository.IMagicReaderRepository
import com.aldisyarif.magicreader.utils.Resources
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteTextUseCase(
    private val repository: IMagicReaderRepository
) {
    operator fun invoke(key: String): Flow<Resources<Task<Void>>>{
        return flow {
            try {
                val response = repository.deleteTextImageById(key)
                emit(Resources.success(response))
            } catch (e: Exception) {
                emit(Resources.error(e.localizedMessage ?: "Unknown Error", null))
            }
        }.flowOn(Dispatchers.IO)
    }
}
package com.aldisyarif.magicreader.usecase

import com.aldisyarif.magicreader.data.model.TextImage
import com.aldisyarif.magicreader.repository.IMagicReaderRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

class GetListTextNoteUseCase(
    private val repository: IMagicReaderRepository
) {
    operator fun invoke() = callbackFlow<Result<MutableList<TextImage>>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val datas = snapshot.children
                val list = mutableListOf<TextImage>()
                datas.forEach {
                    val value = TextImage(
                        textId = it.key,
                        text = it.value.toString()
                    )
                    list.add(value)
                }
                this@callbackFlow.trySendBlocking(Result.success(list))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
            }

        }

        repository.getListNoteText().addValueEventListener(listener)
        awaitClose {
            repository.getListNoteText().removeEventListener(listener)
        }
    }
}
package com.aldisyarif.magicreader.repository

import com.aldisyarif.magicreader.data.model.FirebaseCustomResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MagicReaderRepositoryImpl(
    private val instanceFirebase: FirebaseDatabase,
): IMagicReaderRepository {

    override fun saveTextImage(text: String): FirebaseCustomResponse<Task<Void>> {
        val groups = instanceFirebase.getReference("texts")
        val textId = groups.push().key ?: ""
        return FirebaseCustomResponse(
            textId,
            groups
                .child(textId)
                .setValue(text)
        )
    }

    override fun updateTextImageByKey(key: String, text: String): Task<Void> =
        instanceFirebase.getReference("texts")
            .child(key)
            .setValue(text)

    override fun getListNoteText(): DatabaseReference =
        instanceFirebase
            .getReference("texts")

    override fun deleteTextImageById(key: String): Task<Void> =
        instanceFirebase.getReference("texts")
            .child(key)
            .removeValue()

}
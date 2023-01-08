package com.aldisyarif.magicreader.repository

import com.aldisyarif.magicreader.data.model.FirebaseCustomResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference

interface IMagicReaderRepository {

    fun saveTextImage(text: String): FirebaseCustomResponse<Task<Void>>

    fun updateTextImageByKey(key: String, text: String): Task<Void>

    fun getListNoteText(): DatabaseReference

    fun deleteTextImageById(key: String): Task<Void>
}
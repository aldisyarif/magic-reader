package com.aldisyarif.magicreader.data.model

data class FirebaseCustomResponse <T>(
    val textId: String?,
    val task: T?
)
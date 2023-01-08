package com.aldisyarif.magicreader.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextImage(
    val textId: String?,
    val text: String?,
): Parcelable

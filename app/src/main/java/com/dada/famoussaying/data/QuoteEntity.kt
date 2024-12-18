package com.dada.famoussaying.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quote(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val content: String,
    val date: String
)

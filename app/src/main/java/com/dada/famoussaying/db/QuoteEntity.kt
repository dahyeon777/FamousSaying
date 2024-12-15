package com.dada.famoussaying.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quote(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val content: String,
    val name: String,
    val date: Long
)

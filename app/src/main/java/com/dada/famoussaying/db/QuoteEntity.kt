package com.dada.famoussaying.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Quote(
    @PrimaryKey var id: Int,
    val content: String,
    val date: Long
)

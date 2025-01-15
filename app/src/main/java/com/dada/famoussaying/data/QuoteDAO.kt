package com.dada.famoussaying.data

import androidx.room.*

@Dao
interface QuoteDAO {

    @Insert
    suspend fun insertQuote(quote: Quote)

    @Query ("SELECT * FROM quote ORDER BY id DESC") //역순으로 가져오기
    suspend fun getAllQuotes(): List<Quote>

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Update
    suspend fun updateQuote(quote: Quote)

}


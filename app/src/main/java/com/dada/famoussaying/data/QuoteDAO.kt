package com.dada.famoussaying.data

import androidx.room.*

@Dao
interface QuoteDAO {

    @Insert
    suspend fun insertQuote(quote: Quote)

    @Query ("SELECT * FROM Quote")
    suspend fun getAllQuotes(): List<Quote>

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Update
    suspend fun updateQuote(quote: Quote)

}


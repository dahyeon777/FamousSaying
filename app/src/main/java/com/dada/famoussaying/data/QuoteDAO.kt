package com.dada.famoussaying.data

import androidx.room.*

@Dao
interface QuoteDAO {

    @Insert
    fun insertQuote(quote: Quote)

    @Query ("SELECT * FROM Quote")
    fun getAllQuotes(): List<Quote>

    @Delete
    fun deleteQuote(quote: Quote)

    @Update
    fun updateQuote(quote: Quote)

}


package com.dada.famoussaying

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuoteAdapter (private val data: List<String>): RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>(){

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view,parent,false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.contentTextView.text = data[position]
        holder.dateTextView.text = data[position]
        holder.nameTextView.text = data[position]
    }

    override fun getItemCount(): Int = data.size

}
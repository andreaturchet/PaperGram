package com.ciwrl.papergram.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.R
import com.ciwrl.papergram.Paper

class PaperAdapter(private val papers: List<Paper>) : RecyclerView.Adapter<PaperAdapter.PaperViewHolder>() {

    // ViewHolder: contiene i riferimenti alle View dentro ogni item_paper_card.xml
    class PaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val authorsTextView: TextView = itemView.findViewById(R.id.textViewAuthors)
        val abstractTextView: TextView = itemView.findViewById(R.id.textViewAbstract)
        val keywordsTextView: TextView = itemView.findViewById(R.id.textViewKeywords)
    }

    // Chiamato quando RecyclerView ha bisogno di creare un nuovo ViewHolder (una nuova card)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaperViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_paper_card, parent, false)
        return PaperViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaperViewHolder, position: Int) {
        val currentPaper = papers[position]

        holder.titleTextView.text = currentPaper.title
        holder.authorsTextView.text = currentPaper.authors.joinToString(", ")
        holder.abstractTextView.text = currentPaper.abstractText
        holder.keywordsTextView.text = currentPaper.keywords // O currentPaper.keywords.joinToString(", ") se è una List
    }

    override fun getItemCount(): Int {
        return papers.size
    }
}
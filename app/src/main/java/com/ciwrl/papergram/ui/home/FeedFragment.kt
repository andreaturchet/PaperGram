package com.ciwrl.papergram.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.R
import com.ciwrl.papergram.Paper
import com.ciwrl.papergram.ui.adapter.PaperAdapter

class FeedFragment : Fragment() {

    private lateinit var recyclerViewFeed: RecyclerView
    private lateinit var paperAdapter: PaperAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        recyclerViewFeed = view.findViewById(R.id.recyclerViewFeed)

        //  lista di paper fittizi
        val samplePapers = listOf(
            Paper(
                title = "PaperGram Feed Iniziale!",
                authors = listOf("Io Sviluppatore"),
                abstractText = "Questo è il primo paper visualizzato nel feed del Navigation Drawer Activity. Grandioso!",
                keywords = "Inizio, Feed, Navigazione"
            ),
            Paper(
                title = "Kotlin per Android: Una Guida",
                authors = listOf("Team Android"),
                abstractText = "Esplorando le meraviglie di Kotlin nello sviluppo di app moderne e performanti.",
                keywords = "Kotlin, Android, Performance"
            )
        )

        paperAdapter = PaperAdapter(samplePapers)
        recyclerViewFeed.adapter = paperAdapter
        recyclerViewFeed.layoutManager = LinearLayoutManager(requireContext())

        return view
    }
}
package com.ciwrl.papergram.ui.home

import android.util.Log // Per il logging
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.data.model.api.ArxivEntry
import com.ciwrl.papergram.data.network.RetrofitInstance
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

class HomeViewModel : ViewModel() {

    private val _papers = MutableLiveData<List<Paper>>()
    val papers: LiveData<List<Paper>> = _papers

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchArxivPapers("cat:cs.AI", 10) // Esempio: categoria AI, 10 risultati
    }

    fun fetchArxivPapers(searchQuery: String, maxResults: Int) {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING // Imposta lo stato a caricamento
            try {
                // Chiama il servizio API definito in RetrofitInstance
                val response = RetrofitInstance.arxivApiService.getRecentPapers(
                    searchQuery = searchQuery,
                    maxResults = maxResults
                )

                if (response.isSuccessful) {
                    val arxivFeed = response.body()
                    if (arxivFeed != null && arxivFeed.entries != null) {
                        _papers.value = arxivFeed.entries!!.mapNotNull { entry ->
                            mapArxivEntryToPaper(entry)
                        }
                        _status.value = ApiStatus.DONE // Caricamento completato
                        _errorMessage.value = null // Nessun errore
                    } else {
                        Log.e("HomeViewModel", "Risposta API vuota o senza entries.")
                        _papers.value = emptyList()
                        _status.value = ApiStatus.ERROR
                        _errorMessage.value = "Nessun paper trovato o risposta vuota."
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Errore sconosciuto dalla response"
                    Log.e("HomeViewModel", "Errore API: ${response.code()} - $errorBody")
                    _papers.value = emptyList()
                    _status.value = ApiStatus.ERROR
                    _errorMessage.value = "Errore nel caricamento dei paper: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Eccezione durante la chiamata API: ${e.message}", e)
                _papers.value = emptyList()
                _status.value = ApiStatus.ERROR
                _errorMessage.value = "Errore di connessione o elaborazione dati: ${e.localizedMessage}"
            }
        }
    }

    // Funzione helper per mappare da ArxivEntry (modello API) a Paper (modello UI)
    private fun mapArxivEntryToPaper(entry: ArxivEntry): Paper {
        val title = entry.title.trim().replace("\\s+".toRegex(), " ")
        val abstract = entry.summary.trim().replace("\\s+".toRegex(), " ")
        val authorsList = entry.authors?.map { it.name } ?: listOf("Autore Sconosciuto")
        val keywordsString = entry.categories?.joinToString(", ") { it.term } ?: "N/A"
        val paperId = entry.id.substringAfterLast('/') // Estrai l'ID pulito

        var paperHtmlLink: String? = null
        var paperPdfLink: String? = null

        entry.links?.forEach { link ->
            if (link.rel == "alternate" && link.type == "text/html") {
                paperHtmlLink = link.href
            } else if (link.type == "application/pdf" && (link.rel == "related" || link.titleAttribute == "pdf")) {
                if (link.titleAttribute == "pdf") {
                    paperPdfLink = link.href
                } else if (paperPdfLink == null) {
                    paperPdfLink = link.href
                }
            }
        }

        // Fallback se non abbiamo trovato un link HTML specifico ma c'è un link generico
        if (paperHtmlLink == null && entry.links?.isNotEmpty() == true) {
            paperHtmlLink = entry.links?.first()?.href
        }
        // Fallback per il PDF se non trovato con type="application/pdf" ma l'HTML link punta a un PDF
        if (paperPdfLink == null && paperHtmlLink?.endsWith(".pdf", ignoreCase = true) == true) {
            paperPdfLink = paperHtmlLink
        }


        return Paper(
            id = paperId,
            title = title,
            authors = authorsList,
            abstractText = abstract,
            keywords = keywordsString,
            publishedDate = entry.publishedDate.substringBefore("T"),
            htmlLink = paperHtmlLink,
            pdfLink = paperPdfLink
        )
    }
}
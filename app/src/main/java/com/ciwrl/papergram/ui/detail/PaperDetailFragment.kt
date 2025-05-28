package com.ciwrl.papergram.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.databinding.FragmentPaperDetailBinding
import androidx.core.net.toUri

class PaperDetailFragment : Fragment() {

    private var _binding: FragmentPaperDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PaperDetailFragmentArgs by navArgs()
    private lateinit var currentPaper: Paper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaperDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPaper = args.selectedPaper
        populateUiWithPaperData()
        setupActionButtons()
    }


    private fun populateUiWithPaperData() {
        binding.textViewDetailTitle.text = currentPaper.title
        binding.textViewDetailAuthors.text = currentPaper.authors.joinToString(", ")
        binding.textViewDetailPublishedDate.text = getString(R.string.published_on, currentPaper.publishedDate)
        binding.textViewDetailAbstract.text = currentPaper.abstractText
        binding.textViewDetailKeywords.text = currentPaper.keywords

        // TODO: Impostare lo stato iniziale dell'icona di salvataggio (piena o bordo)
        binding.imageButtonDetailSave.setImageResource(R.drawable.ic_bookmark_border_24dp)
    }

    private fun setupActionButtons() {
        binding.buttonViewFullPaper.setOnClickListener {
            // Prova ad aprire prima il link PDF se disponibile, altrimenti il link HTML
            val urlToOpen = currentPaper.pdfLink ?: currentPaper.htmlLink

            urlToOpen?.let { url ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.e("PaperDetailFragment", "Nessuna applicazione per aprire l'URL: $url", e)
                    Toast.makeText(requireContext(), "Nessuna applicazione trovata per aprire questo tipo di link.", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e("PaperDetailFragment", "Errore nell'aprire l'URL: $url", e)
                    Toast.makeText(requireContext(), "Impossibile aprire il link.", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Log.w("PaperDetailFragment", "Nessun link valido da aprire per il paper: ${currentPaper.title}")
                Toast.makeText(requireContext(), "Nessun link disponibile per questo paper.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageButtonDetailSave.setOnClickListener {
            // TODO: Implementare la logica per salvare/rimuovere il paper
            // E cambiare l'icona di conseguenza
            // Esempio:
            // viewModel.toggleSaveState(currentPaper)
            // Aggiorna l'icona in base al nuovo stato
            Toast.makeText(requireContext(), "Funzionalità Salva/Rimuovi da implementare", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
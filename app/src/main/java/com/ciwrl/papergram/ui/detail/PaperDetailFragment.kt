package com.ciwrl.papergram.ui.detail

import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.databinding.FragmentPaperDetailBinding
import com.ciwrl.papergram.ui.adapter.PdfPageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class PaperDetailFragment : Fragment() {

    private var _binding: FragmentPaperDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PaperDetailFragmentArgs by navArgs()
    private val detailViewModel: PaperDetailViewModel by viewModels()

    private lateinit var currentPaper: Paper
    private lateinit var pdfRecyclerView: RecyclerView
    private lateinit var pdfPageAdapter: PdfPageAdapter
    private lateinit var progressBar: ProgressBar
    private var pdfRenderer: PdfRenderer? = null
    private var tempFile: File? = null
    private var isCurrentlySaved = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaperDetailBinding.inflate(inflater, container, false)
        progressBar = binding.root.findViewById(R.id.progressBar)
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPaper = args.selectedPaper
        setupMenu()
        downloadAndDisplayPdf(currentPaper.pdfLink)

        binding.fabSave.setOnClickListener {
            detailViewModel.toggleSaveState(currentPaper, isCurrentlySaved)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.isPaperSaved(currentPaper.id).collectLatest { isSaved ->
                isCurrentlySaved = isSaved
                if (isSaved) {
                    binding.fabSave.setImageResource(R.drawable.baseline_bookmark_24)
                } else {
                    binding.fabSave.setImageResource(R.drawable.ic_bookmark_border_24dp)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        pdfRecyclerView = binding.root.findViewById(R.id.pdfRecyclerView)
        pdfPageAdapter = PdfPageAdapter(null)
        pdfRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pdfRecyclerView.adapter = pdfPageAdapter
    }

    private fun downloadAndDisplayPdf(pdfUrl: String?) {
        if (pdfUrl == null) {
            Toast.makeText(requireContext(), "Link PDF non disponibile.", Toast.LENGTH_LONG).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                tempFile = File(requireContext().cacheDir, "temp.pdf")
                tempFile?.createNewFile()

                val inputStream: InputStream = URL(pdfUrl).openStream()
                val outputStream = FileOutputStream(tempFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()

                val fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfRenderer = PdfRenderer(fileDescriptor)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    pdfPageAdapter = PdfPageAdapter(pdfRenderer)
                    pdfRecyclerView.adapter = pdfPageAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Errore nel caricamento del PDF.", Toast.LENGTH_LONG).show()
                    Log.e("PaperDetailFragment", "Error loading PDF", e)
                }
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_share -> {
                        sharePaper()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun sharePaper() {
        val shareableLink = currentPaper.htmlLink ?: currentPaper.pdfLink
        if (shareableLink == null) {
            Toast.makeText(requireContext(), "Nessun link da condividere", Toast.LENGTH_SHORT).show()
            return
        }

        val shareText = """
            Dai un'occhiata a questo paper:
            
            *${currentPaper.title}*
            
            Autori: ${currentPaper.authors.joinToString(", ")}
            
            Link: $shareableLink
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Paper interessante: ${currentPaper.title}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(intent, "Condividi paper tramite..."))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            pdfRenderer?.close()
            tempFile?.delete()
        } catch (e: Exception) {
            Log.e("PaperDetailFragment", "Error cleaning up resources", e)
        }
        _binding = null
    }
}
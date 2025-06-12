package com.ciwrl.papergram.ui.detail

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.model.Paper
import com.ciwrl.papergram.databinding.FragmentPaperDetailBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PaperDetailFragment : Fragment() {

    private var _binding: FragmentPaperDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PaperDetailFragmentArgs by navArgs()
    private val detailViewModel: PaperDetailViewModel by viewModels()

    private lateinit var currentPaper: Paper
    private var isCurrentlySaved = false

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaperDetailBinding.inflate(inflater, container, false)

        webView = binding.root.findViewById(R.id.webView)
        progressBar = binding.root.findViewById(R.id.progressBar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPaper = args.selectedPaper
        setupMenu()

        loadPdfInWebView(currentPaper.pdfLink)

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

    private fun loadPdfInWebView(pdfUrl: String?) {
        if (pdfUrl == null) {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Link PDF non disponibile.", Toast.LENGTH_LONG).show()
            return
        }

        val securePdfUrl = pdfUrl.replace("http://", "https://")
        val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$securePdfUrl"


        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)


        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Errore nel caricamento: $description", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("PaperDetailFragment", "Loading URL: $googleDocsUrl")
        webView.loadUrl(googleDocsUrl)
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
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
        if (_binding != null) {
            val webView = binding.root.findViewById<WebView>(R.id.webView)
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.stopLoading()
            webView.settings.javaScriptEnabled = false
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        }
        super.onDestroyView()
        _binding = null
    }
}
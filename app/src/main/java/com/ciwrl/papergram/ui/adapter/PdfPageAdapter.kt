package com.ciwrl.papergram.ui.adapter

import android.annotation.SuppressLint
import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciwrl.papergram.R
import com.github.chrisbanes.photoview.PhotoView
import androidx.core.graphics.createBitmap

class PdfPageAdapter(private var renderer: PdfRenderer?) : RecyclerView.Adapter<PdfPageAdapter.PdfPageViewHolder>() {

    class PdfPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageImage: PhotoView = itemView.findViewById(R.id.page_image)
        val pageNumber: TextView = itemView.findViewById(R.id.page_number)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_page, parent, false)
        return PdfPageViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        renderer?.let {
            val currentPage = it.openPage(position)
            val bitmap = createBitmap(currentPage.width, currentPage.height)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            holder.pageImage.setImageBitmap(bitmap)
            holder.pageNumber.text = "Pagina ${position + 1} / ${itemCount}"

            currentPage.close()
        }
    }

    override fun getItemCount(): Int {
        return renderer?.pageCount ?: 0
    }

}
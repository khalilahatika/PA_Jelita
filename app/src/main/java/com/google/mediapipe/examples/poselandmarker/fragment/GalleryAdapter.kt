package com.google.mediapipe.examples.poselandmarker.fragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.poselandmarker.R

class GalleryAdapter(
    private val imageUris: List<Uri>,
    private val onItemClick: (Uri) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    inner class GalleryViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind(uri: Uri) {
            imageView.setImageURI(uri)
            imageView.setOnClickListener { onItemClick(uri)  }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(300, 300)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(4, 4, 4, 4)
        }
        return GalleryViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount(): Int = imageUris.size
}
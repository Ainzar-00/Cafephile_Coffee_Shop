package com.example.f053.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.f053.R
import com.example.f053.models.GalleryPhoto

class GalleryAdapter(
    private val photos: List<GalleryPhoto>,
    private val onItemClick: (GalleryPhoto) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImage: ImageView = itemView.findViewById(R.id.photoImage)
        val usernameText: TextView = itemView.findViewById(R.id.usernameText)
        val likesText: TextView = itemView.findViewById(R.id.likesText)

        fun bind(photo: GalleryPhoto) {
            photoImage.setImageResource(photo.imageRes)
            usernameText.text = "@${photo.username}"
            likesText.text = "${photo.likes}"

            itemView.setOnClickListener {
                onItemClick(photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_photo, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size
}
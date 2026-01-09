package com.example.f053.screens

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.f053.R
import com.example.f053.models.CoffeeCategory

class CategoriesAdapter(
    private val categories: List<CoffeeCategory>,
    private val onItemClick: (CoffeeCategory) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.categoryCard)
        val iconText: TextView = itemView.findViewById(R.id.categoryIcon)
        val nameText: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: CoffeeCategory) {
            iconText.text = category.icon
            nameText.text = category.name
            cardView.setCardBackgroundColor(Color.parseColor(category.colorHex))

            itemView.setOnClickListener {
                onItemClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
}
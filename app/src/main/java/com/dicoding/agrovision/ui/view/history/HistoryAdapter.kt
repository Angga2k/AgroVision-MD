package com.dicoding.agrovision.ui.view.history

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.AgroVision.R
import com.dicoding.AgroVision.databinding.ItemHistoryBinding
import com.dicoding.agrovision.data.model.HistoryResponse

class HistoryAdapter(
    private var historyList: List<HistoryResponse>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val historyImage: ImageView = view.findViewById(R.id.imageResult)
        val historyResult: TextView = view.findViewById(R.id.textCategoryResult)
        val historyAccuracy: TextView = view.findViewById(R.id.textPercentageResult)
        val historyDate: TextView = view.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        holder.historyResult.text = "Result: ${history.result}"
        holder.historyAccuracy.text = "Accuracy: ${history.accuracy}%"
        holder.historyDate.text = "Date: ${history.date}"

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(history.imageUrl)
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_image_24) // Menggunakan gambar default jika terjadi error
            .into(holder.historyImage)

    }

    override fun getItemCount(): Int = historyList.size

    // Method to update the data in the adapter
    fun updateData(newHistoryList: List<HistoryResponse>) {
        Log.d("HistoryAdapter", "Updating data: ${newHistoryList.size} items")
        this.historyList = newHistoryList
        notifyDataSetChanged()
    }
}


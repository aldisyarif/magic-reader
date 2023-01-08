package com.aldisyarif.magicreader.ui.texts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aldisyarif.magicreader.data.model.TextImage
import com.aldisyarif.magicreader.databinding.ItemTextBinding

class TextNoteAdapter(
    private val listText: MutableList<TextImage>,
    private val callback: (TextImage) -> Unit
): RecyclerView.Adapter<TextNoteAdapter.TextNoteViewHolder>() {

    inner class TextNoteViewHolder(private val binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: TextImage) {
            binding.tvText.text = text.text

            itemView.setOnClickListener {
                callback.invoke(text)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextNoteViewHolder {
        val view = ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TextNoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextNoteViewHolder, position: Int) {
        val text = listText[position]
        holder.bind(text)
    }

    override fun getItemCount(): Int = listText.size

}
package fr.dog.shazanimal.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.dog.shazanimal.R
import fr.dog.shazanimal.analysis.Analysis
import fr.dog.shazanimal.databinding.ItemResultBinding

class ResultAdapter : ListAdapter<Analysis, ResultAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Analysis>() {
        override fun areItemsTheSame(
            oldItem: Analysis,
            newItem: Analysis
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Analysis,
            newItem: Analysis
        ): Boolean = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ItemResultBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ViewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(
        private val binding: ItemResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Analysis) = binding.apply {
            animal.text = item.animal.emoji
            percentage.text = root.context.getString(R.string.result_percentage, item.score)
        }
    }
}
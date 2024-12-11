import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.data.model.Article

class NewsAdapter(private val onItemClick: (String) -> Unit) : PagingDataAdapter<Article, NewsAdapter.NewsViewHolder>(ArticleDiffCallback()) {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.ivArticleImage)

        fun bind(article: Article) {
            titleTextView.text = article.title
            descriptionTextView.text = article.description ?: "No Description"

            // Memuat gambar artikel menggunakan Glide
            Glide.with(imageView.context)
                .load(article.urlToImage) // URL gambar dari API
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.error_image)
                .into(imageView)

            // Set click listener untuk membuka URL artikel
            itemView.setOnClickListener {
                article.url.let { url ->
                    onItemClick(url) // Trigger callback dengan URL artikel
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}

package pack.gallery

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(private val items: List<ImageModel>): RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        var desc: String = "null";
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)

        val holder = ViewHolder(v)

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = items[position]
        holder.imageView.setImageResource(image.imageRes)
        holder.desc = image.description

        holder.imageView.setOnClickListener {
            val context = holder.itemView.context
            val imgShowIntent = Intent(context, ImageActivity::class.java).also {
                it.putExtra("image", image.imageRes)
                it.putExtra("description", image.description)
                startActivity(context, it, null)
            }
        }
    }

    override fun getItemCount() = items.size
}
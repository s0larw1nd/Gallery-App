package pack.gallery.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pack.gallery.models.ImageModel
import pack.gallery.R
import pack.gallery.activities.ImageActivity
import pack.gallery.entities.Image

class ImageAdapter(private var items: List<Image>): RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
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
        val bitmap = BitmapFactory.decodeFile(image.filePath)
        holder.imageView.setImageBitmap(bitmap)
        holder.desc = image.description

        holder.imageView.setOnClickListener {
            val context = holder.itemView.context
            val imgShowIntent = Intent(context, ImageActivity::class.java).also {
                it.putExtra("id", image.id)
                ContextCompat.startActivity(context, it, null)
            }
        }
    }

    override fun getItemCount() = items.size

    fun submitImages(images: List<Image>) {
        items = images
    }

    private fun entityToModel(images: List<Image>) : List<ImageModel> {
        return images.map { ImageModel(filePath = it.filePath, description = it.description, owner = it.owner) }
    }
}
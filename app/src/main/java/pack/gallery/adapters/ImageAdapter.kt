package pack.gallery.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pack.gallery.R
import pack.gallery.activities.ImageActivity
import pack.gallery.entities.Image

class ImageAdapter(
    private var items: List<Image> = emptyList()
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        var desc: String = "null";
    }

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)

        val holder = ViewHolder(v)

        return holder
    }

    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = items[position]
        val bitmap = BitmapFactory.decodeFile(image.filePath)
        holder.imageView.setImageBitmap(bitmap)
        holder.desc = image.description

        holder.imageView.setOnClickListener {
            val context = holder.itemView.context
            Intent(context, ImageActivity::class.java).also {
                it.putExtra("id", image.id)
                ContextCompat.startActivity(context, it, null)
            }
        }
    }

    @Override
    override fun getItemCount() = items.size

    fun submitImages(images: List<Image>) {
        items = images
        notifyDataSetChanged()
    }
}
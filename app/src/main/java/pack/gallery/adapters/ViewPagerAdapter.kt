package pack.gallery.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import pack.gallery.entities.Image
import pack.gallery.fragments.ImageFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var images: List<Image> = emptyList()
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = images.size

    override fun createFragment(position: Int): Fragment {
        val image = images[position]
        return ImageFragment(image.filePath)
    }

    fun submitImages(newPhotos: List<Image>) {
        images = newPhotos
        notifyDataSetChanged()
    }
}
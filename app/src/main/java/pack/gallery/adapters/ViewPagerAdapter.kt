package pack.gallery.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import pack.gallery.entities.Image
import pack.gallery.fragments.PageFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var images: List<Image> = emptyList()
) : FragmentStateAdapter(fragmentActivity) {

    @Override
    override fun getItemCount(): Int = images.size

    @Override
    override fun createFragment(position: Int): Fragment {
        val image = images[position]
        var desc = image.description
        if (desc.length > 80) desc = desc.substring(0,80) + "..."
        return PageFragment(image.filePath, desc)
    }

    fun submitImages(newPhotos: List<Image>) {
        images = newPhotos
        notifyDataSetChanged()
    }
}
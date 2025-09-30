package pack.gallery.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pack.gallery.R

class PageFragment(val filePath: String?, val description: String) : Fragment(R.layout.fragment_page) {

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(R.id.imageContainer, ImageFragment.newInstance(filePath))
            .replace(R.id.textContainer, TextFragment.newInstance(description))
            .commit()
    }
}
package pack.gallery.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import pack.gallery.R

class PageFragment() : Fragment(R.layout.fragment_page) {

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(R.id.imageContainer, ImageFragment.newInstance(requireArguments().getString("filePath")))
            .replace(R.id.textContainer, TextFragment.newInstance(requireArguments().getString("description").toString()))
            .commit()
    }

    companion object {
        fun newInstance(filePath: String?, description: String): PageFragment {
            return PageFragment().apply {
                arguments = bundleOf("filePath" to filePath, "description" to description)
            }
        }
    }
}
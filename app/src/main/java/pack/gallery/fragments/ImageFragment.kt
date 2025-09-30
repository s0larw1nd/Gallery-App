package pack.gallery.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import pack.gallery.R

class ImageFragment : Fragment(R.layout.fragment_image) {

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        imageView.setImageBitmap(BitmapFactory.decodeFile(requireArguments().getString("filePath")))
    }

    companion object {
        fun newInstance(filePath: String?): ImageFragment {
            return ImageFragment().apply {
                arguments = bundleOf("filePath" to filePath)
            }
        }
    }
}
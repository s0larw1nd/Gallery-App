package pack.gallery.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import pack.gallery.R

class TextFragment : Fragment(R.layout.fragment_description) {

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = view.findViewById<TextView>(R.id.textView)
        text.setText(requireArguments().getString("description"))
    }

    companion object {
        fun newInstance(description: String): TextFragment {
            return TextFragment().apply {
                arguments = bundleOf("description" to description)
            }
        }
    }
}
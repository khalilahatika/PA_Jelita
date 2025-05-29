package com.google.mediapipe.examples.poselandmarker.fragment

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.mediapipe.examples.poselandmarker.databinding.FragmentFullscreenImageBinding
import java.io.File

class FullscreenImageFragment : Fragment() {

    private lateinit var binding: FragmentFullscreenImageBinding
    private lateinit var imageUri: Uri
    private lateinit var onImageDeleted: () -> Unit



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUri = Uri.parse(requireArguments().getString("imageUri"))
        binding.fullscreenImageView.setImageURI(imageUri)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnDelete.setOnClickListener {
            try {
                val rowsDeleted = requireContext().contentResolver.delete(imageUri, null, null)
                if (rowsDeleted > 0) {
                    onImageDeleted()
                } else {
                    // Tambahkan log jika gagal
                    android.util.Log.e("DeleteImage", "Gagal menghapus image: $imageUri")
                }
            } catch (e: Exception) {
                android.util.Log.e("DeleteImage", "Error deleting image: ${e.message}")
            }
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(uri: Uri, onImageDeleted: () -> Unit): FullscreenImageFragment {
            val fragment = FullscreenImageFragment()
            fragment.arguments = Bundle().apply {
                putString("imageUri", uri.toString())
            }
            fragment.onImageDeleted = onImageDeleted
            return fragment
        }
    }
}

package com.aldisyarif.magicreader.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aldisyarif.magicreader.R
import com.aldisyarif.magicreader.data.model.TextImage
import com.aldisyarif.magicreader.databinding.FragmentHomeBinding
import com.aldisyarif.magicreader.ui.detail.DetailTextFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var isCreateDialog = false
    private var isNavigate = false
    private var text = ""

    private var bitmap: Bitmap? = null
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponent()

    }

    private fun initComponent() {
        toolbarView()
        chooseSampleImage()
        observeViewModel()
    }

    private fun toolbarView() {
        binding.btnToListNote.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_textsFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.successProcess.collectLatest {
                it?.let {
                    if (it.text.isEmpty()) {
                        binding.tvResult.text = ""
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.text_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (!isCreateDialog){
                            showConfirmDialog(it.text)
                            binding.tvResult.text = it.text
                            text = it.text

                            isCreateDialog = true
                        }

                    }
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.failedProcess.collectLatest {
                if (it != null) Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loadingProcess.collectLatest {
                if (it == true){
                    binding.stateLoading.visibility = View.VISIBLE
                } else {
                    binding.stateLoading.visibility = View.GONE
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.successSaveText.collectLatest {
                it?.let {
                    if (!isNavigate){
                        val dataText = TextImage(
                            textId = it,
                            text = text
                        )
                        val bundle = bundleOf(DetailTextFragment.TEXT_OBJ to dataText)
                        findNavController().navigate(R.id.action_homeFragment_to_detailTextFragment, bundle)

                        isNavigate = true
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.failedSaveText.collectLatest { msg ->
                msg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loadingSaveText.collectLatest {
                if (it == true){
                    binding.stateLoading.visibility = View.VISIBLE
                } else {
                    binding.stateLoading.visibility = View.GONE
                }
            }
        }

    }

    private fun chooseSampleImage() {
        binding.apply {
            btnProcess.setOnClickListener {
                bitmap?.let {
                    isCreateDialog = false
                    isNavigate = false
                    viewModel.processImage(it)
                }
            }
            imgSampleOne.setOnClickListener {
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_example)
                bitmap?.let {
                    btnProcess.visibility = View.VISIBLE
                    tvResult.text = ""
                    imgResult.setImageBitmap(bitmap)
                }
            }
            imgSampleTwo.setOnClickListener {
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.quote_madara)
                bitmap?.let {
                    btnProcess.visibility = View.VISIBLE
                    tvResult.text = ""
                    imgResult.setImageBitmap(bitmap)
                }
            }
            imgSampleThree.setOnClickListener {
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.quote_keyle)
                bitmap?.let {
                    btnProcess.visibility = View.VISIBLE
                    tvResult.text = ""
                    imgResult.setImageBitmap(bitmap)
                }
            }
            captureImageFab.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, getString(R.string.choose_image))
                launcherIntentGallery.launch(chooser)
            }

        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            val bitmapGallery = uriToBitmap(selectedImg, requireContext())

            bitmap = bitmapGallery
            binding.apply {
                bitmap?.let {
                    btnProcess.visibility = View.VISIBLE
                    tvResult.text = ""
                    imgResult.setImageBitmap(bitmap)
                    isCreateDialog = false
                }
            }
        }
    }


    private fun uriToBitmap(uri: Uri, context: Context): Bitmap {
        val bitmap: Bitmap
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        return bitmap
    }

    private fun showConfirmDialog(text: String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.text_detected))
        builder.setMessage(getString(R.string.message_success))
        builder.setPositiveButton(getString(R.string.save)) { dialogInterface,i ->
            viewModel.saveTextInDB(text)

            Toast.makeText(requireContext(), getString(R.string.save_successfully), Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(getString(R.string.dont_save), null)
        builder.show()
    }

}
package com.aldisyarif.magicreader.ui.detail

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aldisyarif.magicreader.R
import com.aldisyarif.magicreader.data.model.TextImage
import com.aldisyarif.magicreader.databinding.FragmentDetailTextBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailTextFragment : Fragment() {

    private var textId = ""

    private val viewModel: DetailTextViewModel by viewModels()

    private lateinit var binding: FragmentDetailTextBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailTextBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponent()
    }

    private fun initComponent() {
        toolbarView()
        initOnBackPress()
        bodyView()
        observeViewModel()
        buttonCopyPasteView()
    }

    private fun buttonCopyPasteView() {
        binding.apply {
            btnCopy.setOnClickListener {
                //clipboard manager to copy text
                val textToCopy = etResultText.text
                val clipBoardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", textToCopy)
                clipBoardManager.setPrimaryClip(clipData)

                Toast.makeText(requireContext(), getString(R.string.message_copied), Toast.LENGTH_SHORT)
                    .show()
            }

            btnPaste.setOnClickListener {
                val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val stringBuilder = StringBuilder(etResultText.text.toString())
                etResultText.setText(stringBuilder.append(clipboardManager.primaryClip?.getItemAt(0)?.text.toString()))
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.successUpdateText.collectLatest {
                if (it == true) Toast.makeText(requireContext(), getString(R.string.updated_successfully), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loadingUpdateText.collectLatest {
                if (it == true){
                    binding.stateLoading.visibility = View.VISIBLE
                } else {
                    binding.stateLoading.visibility = View.GONE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.failedUpdateText.collectLatest { msg ->
                msg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.successDeleteText.collectLatest {
                if (it == true) {
                    Toast.makeText(requireContext(), getString(R.string.delete_this_text), Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loadingDeleteText.collectLatest {
                if (it == true){
                    binding.stateLoading.visibility = View.VISIBLE
                } else {
                    binding.stateLoading.visibility = View.GONE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.failedDeleteText.collectLatest { msg ->
                msg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun bodyView() {
        arguments?.let { arg ->
            val data = arg.getParcelable(TEXT_OBJ) as? TextImage
            textId = data?.textId ?: ""

            binding.apply {
                etResultText.setText(data?.text)
            }
        }

    }

    private fun toolbarView() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnUpdateData.setOnClickListener {
            showConfirmDialog(getString(R.string.save_this_text)){
                viewModel.updateText(textId, binding.etResultText.text.toString())
            }
        }
        binding.btnDelete.setOnClickListener {
            showConfirmDialog(getString(R.string.delete_this_text)){
                viewModel.deleteText(textId)
            }
        }
    }

    private fun initOnBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }

            }
        )
    }

    private fun showConfirmDialog(title: String, action: () -> Unit){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, i ->
            action.invoke()
        }
        builder.setNegativeButton(getString(R.string.no), null)
        builder.show()
    }

    companion object {
        const val TEXT_OBJ = "text"
    }

}
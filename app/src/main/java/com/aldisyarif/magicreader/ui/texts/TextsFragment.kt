package com.aldisyarif.magicreader.ui.texts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aldisyarif.magicreader.R
import com.aldisyarif.magicreader.data.model.TextImage
import com.aldisyarif.magicreader.databinding.FragmentTextsBinding
import com.aldisyarif.magicreader.enums.RequestStatus
import com.aldisyarif.magicreader.ui.detail.DetailTextFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TextsFragment : Fragment() {

    private lateinit var adapter: TextNoteAdapter
    private val viewModel: TextsViewModel by viewModels()

    private lateinit var binding: FragmentTextsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponent()
    }

    private fun initComponent() {
        toolbarView()
        initViewModel()
        observeViewModel()
    }

    private fun toolbarView() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.textsNoteResponse.collectLatest {
                when(it.requestStatus){
                    RequestStatus.LOADING -> {
                        showLoading(true)
                    }
                    RequestStatus.SUCCESS -> {
                        showLoading(false)
                        showListTextNote(it.data)
                    }
                    RequestStatus.ERROR -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showListTextNote(list: MutableList<TextImage>?) {
        binding.apply {
            adapter = TextNoteAdapter(list ?: mutableListOf()){
                val bundle = bundleOf(DetailTextFragment.TEXT_OBJ to it)
                findNavController().navigate(R.id.action_textsFragment_to_detailTextFragment, bundle)
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }
    }


    private fun showLoading(state: Boolean) {
        if (state){
            binding.stateLoading.visibility = View.VISIBLE
        } else {
            binding.stateLoading.visibility = View.GONE
        }
    }

    private fun initViewModel() {
        viewModel.getListNoteText()
    }
}
package com.example.testtask.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.testtask.gifscreen.GifFragment
import com.example.testtask.R
import com.example.testtask.databinding.FragmentGifListBinding
import com.example.testtask.network.ApiHelperImpl
import com.example.testtask.MainRepository
import com.example.testtask.network.RetrofitBuilder
import kotlinx.coroutines.launch

class GifListFragment : Fragment() {
    private val adapter by lazy {
        GifListRecyclerAdapter(onItemClicked)
    }

    private val onItemClicked: (String) -> Unit = { gifUrl ->
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.fragment_view, GifFragment(gifUrl))
            addToBackStack(null)
            commit()
        }
    }

    private val viewModel: GifListViewModel by viewModels {
        GifListViewModel.provideFactory(
            MainRepository(ApiHelperImpl(RetrofitBuilder.apiService))
        )
    }

    private var binding: FragmentGifListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGifListBinding.inflate(LayoutInflater.from(context), container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        changePage()
        uiStateListener()
    }

    private fun toastShow(message: String, lengthShort: Int = Toast.LENGTH_SHORT) {
        if (lengthShort == Toast.LENGTH_LONG) {
            binding?.errorImg?.visibility = View.VISIBLE
            binding?.gifList?.visibility = View.INVISIBLE
        }
        Toast.makeText(this.context?.applicationContext, message, lengthShort).show()
    }

    private fun initUi() {
        binding?.apply {
            gifList.adapter = adapter
            lifecycleScope.launch {
                viewModel.listState.collect { values ->
                    adapter.submitList(values.list)
                }
            }
        }
    }

    private fun changePage() {
        binding?.apply {
            buttonNextPage.setOnClickListener {
                viewModel.getGifNextPage()
            }
            buttonPrevPage.setOnClickListener {
                viewModel.getGifPrevPage()
            }
        }
    }

    private fun uiStateListener() {
        lifecycleScope.launch {
            viewModel.uiState.collect { value ->
                statePageListener(value)
                stateResponseAndError(value)
            }
        }
    }

    private fun statePageListener(value: UiState) {
        when (value.prevPage) {
            true -> binding?.buttonPrevPage?.isEnabled = true
            false -> binding?.buttonPrevPage?.isEnabled = false
        }
        when (value.nextPage) {
            true -> binding?.buttonNextPage?.isEnabled = true
            false -> binding?.buttonNextPage?.isEnabled = false
        }
    }

    private fun stateResponseAndError(value: UiState) {
        when (value.response) {
            UiState.StatusResponse.UNAUTHORIZED -> toastShow(getString(R.string.status_unauthorized))
            UiState.StatusResponse.FORBIDDEN -> toastShow(getString(R.string.status_forbidden))
            UiState.StatusResponse.TOOMANYREQUESTS -> toastShow(getString(R.string.status_too_many_request))
            UiState.StatusResponse.OK, UiState.StatusResponse.NONE -> {}
        }
        when (value.error) {
            UiState.ErrorStatus.FAIL_CONNECT -> toastShow(
                getString(R.string.error_fail_connect),
                Toast.LENGTH_LONG
            )

            UiState.ErrorStatus.OTHER -> toastShow(
                getString(R.string.error_other),
                Toast.LENGTH_LONG
            )

            UiState.ErrorStatus.NONE -> {}
        }
    }
}


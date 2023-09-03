package com.example.testtask.gifscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.testtask.databinding.FragmentGifFullsizeBinding

class GifScreen(private val gifUrl: String) : Fragment() {

    private var binding: FragmentGifFullsizeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGifFullsizeBinding.inflate(LayoutInflater.from(context), container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding?.let {
            Glide.with(requireActivity().applicationContext).load(gifUrl)
                .into(it.fullsizeGifField)
        }
    }
}
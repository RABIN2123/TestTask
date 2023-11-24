package com.example.testtask.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testtask.databinding.FragmentGifItemBinding


class GifListRecyclerAdapter(
    private val onItemClicked: (String) -> Unit
) :
    ListAdapter<GifItem, GifListRecyclerAdapter.MyViewHolder>(ItemDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            FragmentGifItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, parent.context.applicationContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(currentList[position], onItemClicked)
    }

    class MyViewHolder(
        private val binding: FragmentGifItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GifItem, onItemClicked: (String) -> Unit) {
            with(binding) {
                Log.d("TAG!", item.smallImageUrl)
                Glide.with(context).load(item.smallImageUrl).into(gifField)
                root.setOnClickListener {
                    onItemClicked(item.bigImageUrl)
                }
            }

        }
    }

    class ItemDiffCallBack : DiffUtil.ItemCallback<GifItem>() {
        override fun areItemsTheSame(oldItem: GifItem, newItem: GifItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GifItem, newItem: GifItem): Boolean {
            return oldItem == newItem
        }
    }
}
package com.ihsanmkls.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ihsanmkls.storyapp.BuildConfig
import com.ihsanmkls.storyapp.data.api.Story
import com.ihsanmkls.storyapp.databinding.ItemRowStoryBinding
import com.ihsanmkls.storyapp.view.story.DetailStoryActivity

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(EXTRA_STORY, data)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.photoStory, "photo"),
                        Pair(binding.authorTextView, "name"),
                        Pair(binding.descriptionTextView, "description")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

            binding.apply {
                Glide.with(itemView)
                    .load(data.photoUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(photoStory)
                authorTextView.text = data.name
                descriptionTextView.text = data.description
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data)
    }

    companion object {
        const val EXTRA_STORY = "Story"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
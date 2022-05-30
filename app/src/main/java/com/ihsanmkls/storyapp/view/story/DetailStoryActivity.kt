package com.ihsanmkls.storyapp.view.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ihsanmkls.storyapp.R
import com.ihsanmkls.storyapp.adapter.StoryAdapter.Companion.EXTRA_STORY
import com.ihsanmkls.storyapp.data.api.Story
import com.ihsanmkls.storyapp.databinding.ActivityDetailStoryBinding
import com.ihsanmkls.storyapp.helper.DateFormat.dateFormatting

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupView()
    }

    private fun setupView() {
        val story = intent.getParcelableExtra<Story>(EXTRA_STORY) as Story

        binding.apply {
            Glide.with(this@DetailStoryActivity)
                .load(story.photoUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(detailPhotoStory)

            detailNameTextView.text = story.name
            detailDateTextView.text = getString(R.string.uploaded_at, story.createdAt?.dateFormatting())
            detailDescTextView.text = story.description
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

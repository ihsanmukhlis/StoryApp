package com.ihsanmkls.storyapp.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihsanmkls.storyapp.R
import com.ihsanmkls.storyapp.adapter.LoadingStateAdapter
import com.ihsanmkls.storyapp.adapter.StoryAdapter
import com.ihsanmkls.storyapp.data.UserPreferences
import com.ihsanmkls.storyapp.databinding.ActivityMainBinding
import com.ihsanmkls.storyapp.view.ViewModelFactory
import com.ihsanmkls.storyapp.view.login.LoginActivity
import com.ihsanmkls.storyapp.view.maps.MapsActivity
import com.ihsanmkls.storyapp.view.story.AddNewStoryActivity
import com.ihsanmkls.storyapp.view.story.StoryViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModel.ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        setupAction()
    }

    private fun getData() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                storyViewModel.getStories(user.token).observe(this) { story ->
                    adapter.submitData(lifecycle, story)
                }
                isLoading(false)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            fabAddNewStory.setOnClickListener{
                val intent = Intent(this@MainActivity, AddNewStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.map -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }R.id.logout -> {
                mainViewModel.logout()
                finish()
                true
            }
            else -> true
        }
    }

    private fun isLoading(progressState: Boolean) { binding.progressBar.visibility = if (progressState) View.VISIBLE else View.GONE }
}
package com.ihsanmkls.storyapp.view.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ihsanmkls.storyapp.R
import com.ihsanmkls.storyapp.api.ApiConfig
import com.ihsanmkls.storyapp.data.UserPreferences
import com.ihsanmkls.storyapp.data.api.GeneralResponse
import com.ihsanmkls.storyapp.databinding.ActivityAddNewStoryBinding
import com.ihsanmkls.storyapp.utils.rotateBitmap
import com.ihsanmkls.storyapp.utils.uriToFile
import com.ihsanmkls.storyapp.view.ViewModelFactory
import com.ihsanmkls.storyapp.view.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddNewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewStoryBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var location: Location? = null

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10

        private const val TAG = "AddNewStoryActivity"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add New Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        setupAction()
        getMyLocation()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
    }

    private fun setupAction() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }

        binding.apply {
            cameraButton.setOnClickListener { startCameraX() }
            galleryButton.setOnClickListener { startGallery() }
            uploadButton.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) { uploadImage() } }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddNewStoryActivity)

            getFile = myFile

            binding.imgPreviewPhoto.setImageURI(selectedImg)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }
    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.imgPreviewPhoto.setImageBitmap(result)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    location = loc
                } else {
                    Toast.makeText(this@AddNewStoryActivity, "Location undetected!. Please try again", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = getFile

            val description = binding.descriptionEditText.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val lat = location?.latitude?.toFloat()
            val lon = location?.longitude?.toFloat()
            val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file?.name,
                requestImageFile!!
            )

            isLoading(true)

            viewModel.getUser().observe(this) { user ->
                ApiConfig.getApiService().addNewStory("Bearer ${user.token}", description, lat, lon, imageMultipart).enqueue(object :
                    Callback<GeneralResponse> {
                    override fun onResponse(
                        call: Call<GeneralResponse>,
                        response: Response<GeneralResponse>
                    ){
                        if (response.isSuccessful && !response.body()?.error!!) {
                            isLoading(false)

                            Toast.makeText(this@AddNewStoryActivity, "Your story has been uploaded", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            isLoading(false)

                            Toast.makeText(this@AddNewStoryActivity, "Failed to upload your story, please check the description", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                        isLoading(false)

                        Toast.makeText(this@AddNewStoryActivity, getString(R.string.system_error), Toast.LENGTH_SHORT).show()
                        Log.d(TAG, t.message.toString())
                    }
                })
            }
        } else {
            Toast.makeText(this@AddNewStoryActivity, "Please input your file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLoading(progressState: Boolean): ActivityAddNewStoryBinding {
        return binding.apply {
            when {
                progressState -> {
                    uploadProgress.visibility = View.VISIBLE
                    uploadButton.visibility = View.INVISIBLE
                }
                else -> {
                    uploadProgress.visibility = View.GONE
                    uploadButton.visibility = View.VISIBLE
                }
            }
        }
    }
}
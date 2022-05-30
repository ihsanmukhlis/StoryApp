package com.ihsanmkls.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.ihsanmkls.storyapp.R
import com.ihsanmkls.storyapp.api.ApiConfig
import com.ihsanmkls.storyapp.data.UserPreferences
import com.ihsanmkls.storyapp.data.api.Login
import com.ihsanmkls.storyapp.data.api.LoginResponse
import com.ihsanmkls.storyapp.data.api.User
import com.ihsanmkls.storyapp.databinding.ActivityLoginBinding
import com.ihsanmkls.storyapp.view.ViewModelFactory
import com.ihsanmkls.storyapp.view.main.MainActivity
import com.ihsanmkls.storyapp.view.register.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        loginViewModel = ViewModelProvider(
            this@LoginActivity,
            ViewModelFactory(pref)
        )[LoginViewModel::class.java]
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isLoading(progressState: Boolean): ActivityLoginBinding {
        return binding.apply {
            when {
                progressState -> {
                    loginProgress.visibility = View.VISIBLE
                    loginButton.visibility = View.INVISIBLE
                }
                else -> {
                    loginProgress.visibility = View.GONE
                    loginButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    loginButton.isEnabled = passwordEditText.text?.length!! >= 6
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    loginButton.isEnabled = passwordEditText.text?.length!! >= 6
                }

                override fun afterTextChanged(p0: Editable?) {
                    loginButton.isEnabled = passwordEditText.text?.length!! >= 6
                }
            })

            loginButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                when {
                    email.isEmpty() -> {
                        emailEditTextLayout.error = "Email cannot be empty!"
                    }
                    !isValidEmail(email) -> {
                        emailEditTextLayout.error = "Please input valid email!"
                    }
                    password.isEmpty() -> {
                        passwordEditTextLayout.error = "Password cannot be empty!"
                    }
                    else -> {
                        emailEditTextLayout.error = ""
                        passwordEditTextLayout.error = ""

                        isLoading(true)

                        ApiConfig.getApiService().userLogin(Login(email, password)).enqueue(object :
                            Callback<LoginResponse> {
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful && !response.body()?.error!!) {
                                    val getData = response.body()?.loginResult as User

                                    loginViewModel.setUser(User(true, getData.userId, getData.name, getData.token))

                                    isLoading(false)

                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    isLoading(false)

                                    AlertDialog.Builder(this@LoginActivity).apply {
                                        setTitle(R.string.title_msg_error)
                                        setMessage("Login failed, make sure the data you entered is correct.")
                                        create()
                                        show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                isLoading(false)

                                AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle(R.string.title_msg_error)
                                    setMessage(R.string.system_error)
                                    create()
                                    show()
                                }
                                Log.d(TAG, t.message.toString())
                            }
                        })
                    }
                }
            }

            registerTextView.setOnClickListener {
                val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(registerIntent)
            }
        }
    }

    private fun playAnimation() {
        binding.apply {
            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val title = ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(200)
            val emailTextView = ObjectAnimator.ofFloat(emailTextView, View.ALPHA, 1f).setDuration(200)
            val emailEditTextLayout = ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
            val passwordTextView = ObjectAnimator.ofFloat(passwordTextView, View.ALPHA, 1f).setDuration(200)
            val passwordEditTextLayout = ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
            val titleRegisterHintTextView = ObjectAnimator.ofFloat(titleRegisterHintTextView, View.ALPHA, 1f).setDuration(200)
            val registerTextView = ObjectAnimator.ofFloat(registerTextView, View.ALPHA, 1f).setDuration(200)
            val login = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(title, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, titleRegisterHintTextView, registerTextView, login)
                startDelay = 200
            }.start()
        }
    }

    companion object{
        private const val TAG = "LoginActivity"
    }
}
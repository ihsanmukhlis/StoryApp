package com.ihsanmkls.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.ihsanmkls.storyapp.R
import com.ihsanmkls.storyapp.api.ApiConfig
import com.ihsanmkls.storyapp.data.api.*
import com.ihsanmkls.storyapp.databinding.ActivityRegisterBinding
import com.ihsanmkls.storyapp.view.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
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

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isLoading(progressState: Boolean): ActivityRegisterBinding {
        return binding.apply {
            when {
                progressState -> {
                    registerProgress.visibility = View.VISIBLE
                    registerButton.visibility = View.INVISIBLE
                }
                else -> {
                    registerProgress.visibility = View.GONE
                    registerButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    registerButton.isEnabled = passwordEditText.text?.length!! >= 6
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    registerButton.isEnabled = passwordEditText.text?.length!! >= 6
                }

                override fun afterTextChanged(p0: Editable?) {
                    registerButton.isEnabled = passwordEditText.text?.length!! >= 6
                }
            })

            registerButton.setOnClickListener {
                val name = binding.nameEditText.text.toString().trim()
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                when {
                    name.isEmpty() -> {
                        nameEditTextLayout.error = "Name cannot be empty!"
                    }
                    email.isEmpty() -> {
                        emailEditTextLayout.error = "Email cannot be empty!"
                    }
                    !isValidEmail(email) -> {
                        emailEditTextLayout.error = "Please input valid email!"
                    }
                    password.isEmpty() -> {
                        passwordEditTextLayout.error = "Password cannot be empty!"
                    }
                    password.length < 6 -> {
                        passwordEditTextLayout.error = "Please input minimum 6 characters!"
                    }
                    else -> {
                        nameEditTextLayout.error = ""
                        emailEditTextLayout.error = ""
                        passwordEditTextLayout.error = ""

                        isLoading(true)

                        ApiConfig.getApiService().userRegister(Register(name, email, password)).enqueue(object :
                            Callback<GeneralResponse> {
                            override fun onResponse(
                                call: Call<GeneralResponse>,
                                response: Response<GeneralResponse>
                            ) {
                                if (response.isSuccessful && !response.body()?.error!!) {
                                    isLoading(false)

                                    AlertDialog.Builder(this@RegisterActivity).apply {
                                        setTitle(R.string.title_msg_success)
                                        setMessage("Register successfully. Please click next to login your account.")
                                        setPositiveButton("Next") { _, _ ->
                                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(intent)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                } else {
                                    isLoading(false)

                                    AlertDialog.Builder(this@RegisterActivity).apply {
                                        setTitle(R.string.title_msg_error)
                                        setMessage("Register failed, make sure the email you entered is not registered.")
                                        create()
                                        show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                                isLoading(false)

                                AlertDialog.Builder(this@RegisterActivity).apply {
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

            loginTextView.setOnClickListener {
                val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(loginIntent)
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
            val nameTextView = ObjectAnimator.ofFloat(nameTextView, View.ALPHA, 1f).setDuration(200)
            val nameEditTextLayout = ObjectAnimator.ofFloat(nameEditTextLayout, View.ALPHA, 1f).setDuration(200)
            val emailTextView = ObjectAnimator.ofFloat(emailTextView, View.ALPHA, 1f).setDuration(200)
            val emailEditTextLayout = ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
            val passwordTextView = ObjectAnimator.ofFloat(passwordTextView, View.ALPHA, 1f).setDuration(200)
            val passwordEditTextLayout = ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
            val titleLoginHintTextView = ObjectAnimator.ofFloat(titleLoginHintTextView, View.ALPHA, 1f).setDuration(200)
            val loginTextView = ObjectAnimator.ofFloat(loginTextView, View.ALPHA, 1f).setDuration(200)
            val register = ObjectAnimator.ofFloat(registerButton, View.ALPHA, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(title, nameTextView, nameEditTextLayout, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, titleLoginHintTextView, loginTextView, register)
                startDelay = 200
            }.start()
        }
    }

    companion object{
        private const val TAG = "RegisterActivity"
    }
}
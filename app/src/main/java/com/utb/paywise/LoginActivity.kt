package com.utb.paywise

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import com.utb.paywise.databinding.ActivityLoginBinding
import io.reactivex.Observable

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Authentication
        auth = FirebaseAuth.getInstance()

//      Username Validation
        val usernameStream = RxTextView.textChanges(binding.editextUsernameLogin)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        usernameStream.subscribe() {
            usernamePasswordValidation(it, "Email/Username")
        }

//        Password Validation
        val passwordStream = RxTextView.textChanges(binding.edittextPasswordLogin)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        passwordStream.subscribe() {
            usernamePasswordValidation(it, "Password")
        }

        val invalidFieldStream = Observable.combineLatest(
            usernameStream,
            passwordStream,
            {
                    usernameInvalid: Boolean,
                    passwordInvalid: Boolean ->
                    !usernameInvalid && !passwordInvalid
            })

        invalidFieldStream.subscribe { isValid ->
            if (isValid) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
            } else {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)

            }
        }

//        Onlick
        binding.btnLogin.setOnClickListener {
            val email = binding.editextUsernameLogin.text.toString().trim()
            val password = binding.edittextPasswordLogin.text.toString().trim()
            loginUser(email, password)
        }

        binding.haventAccLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.forgotPwLogin.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    private fun usernamePasswordValidation(isNotValid: Boolean, text: String) {
        if (text == "Email/Username")
            binding.editextUsernameLogin.error =
                if (isNotValid)
                    "$text tidak boleh kosong"
                else null
        else if (text == "Password")
            binding.edittextPasswordLogin.error =
                if (isNotValid)
                    "$text tidak boleh kosong"
                else null
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { login ->
                if (login.isSuccessful) {
                    Intent(this, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, login.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }


}





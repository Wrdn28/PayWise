package com.utb.paywise

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jakewharton.rxbinding2.widget.RxTextView
import com.utb.paywise.databinding.ActivityLoginBinding
import io.reactivex.Observable

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.haventAccLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
}
package com.utb.paywise

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import com.utb.paywise.databinding.ActivityRegisterBinding
import io.reactivex.Observable

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Authtentication
        auth = FirebaseAuth.getInstance()

//        Name Validation
        val nameStream = RxTextView.textChanges(binding.edittextNameRegister)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe() {
            nameValidation(it)
        }

//        Email Validation
        val emailStream = RxTextView.textChanges(binding.edittextEmailRegister)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe() {
            emailValidation(it)
        }

//        Username Validation
        val usernameStream = RxTextView.textChanges(binding.edittextUsernameRegister)
            .skipInitialValue()
            .map { username ->
                username.length < 6
            }
        usernameStream.subscribe() {
            usernamePasswordValidation(it, "Username")
        }

//        Password Validation
        val passwordStream = RxTextView.textChanges(binding.edittextPasswordRegister)
            .skipInitialValue()
            .map { username ->
                username.length < 8
            }
        passwordStream.subscribe() {
            usernamePasswordValidation(it, "Password")
        }

//        Confirm Password Validation
        val passwordConfirmStream = Observable.merge(
            RxTextView.textChanges(binding.edittextPasswordRegister)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.edittextConfirmpasswordRegister.text.toString()
                },
            RxTextView.textChanges(binding.edittextConfirmpasswordRegister)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.edittextPasswordRegister.text.toString()
                })
        passwordConfirmStream.subscribe() {
            passwordConfirmValidation(it)
        }

//        Button Active or Inactive
        val invalidFieldStream = Observable.combineLatest(
            nameStream,
            emailStream,
            usernameStream,
            passwordStream,
            passwordConfirmStream,
            {
                nameInvalid: Boolean,
                emailInvalid: Boolean,
                usernameInvalid: Boolean,
                passwordInvalid: Boolean,
                confirmPasswordInvalid: Boolean ->
                !nameInvalid && !emailInvalid && !usernameInvalid && !passwordInvalid && !confirmPasswordInvalid
            })

        invalidFieldStream.subscribe { isValid ->
            if (isValid) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
            } else {
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

//        Onclick
        binding.btnRegister.setOnClickListener {
            val email = binding.edittextEmailRegister.text.toString().trim()
            val password = binding.edittextPasswordRegister.text.toString().trim()
            registerUser(email, password)
        }

        binding.haveAccRegiter.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun nameValidation(isNotValid: Boolean) {
        binding.edittextNameRegister.error =
            if (isNotValid)
                "Nama tidak boleh kosong"
            else null
    }

    private fun usernamePasswordValidation(isNotValid: Boolean, text: String) {
        if (text == "Username")
            binding.edittextUsernameRegister.error =
                if (isNotValid)
                    "$text harus lebih dari 6 Huruf"
                else null
                else if (text == "Password")
                    binding.edittextPasswordRegister.error =
                        if (isNotValid)
                            "$text harus lebih dari 8 huruf"
                        else null
    }

    private fun emailValidation(isNotValid: Boolean) {
        binding.edittextEmailRegister.error =
            if (isNotValid)
                "Email tidak valid"
            else null
    }

    private fun passwordConfirmValidation(isNotValid: Boolean) {
        binding.edittextConfirmpasswordRegister.error =
            if (isNotValid)
                "Password tidak sama"
            else null
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

}
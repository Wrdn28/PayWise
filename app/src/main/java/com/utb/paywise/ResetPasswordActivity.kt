package com.utb.paywise

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import com.utb.paywise.databinding.ActivityResetPasswordBinding

@SuppressLint("CheckResult")
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Authentication
        auth = FirebaseAuth.getInstance()

//        Email Validation
        val emailStream = RxTextView.textChanges(binding.edittextResetPwEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe() {
            emailValidation(it)
        }

//        Reset Password Button
        binding.btnResetPw.setOnClickListener {
            val email = binding.edittextResetPwEmail.text.toString().trim()
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { reset ->
                    if (reset.isSuccessful) {
                        Intent(this, LoginActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            Toast.makeText(this, "Email reset password berhasil di kirim ke email anda!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, reset.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.backLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun emailValidation(isNotValid: Boolean) {
        if (isNotValid) {
            binding.edittextResetPwEmail.error = "Email tidak valid!"
            binding.btnResetPw.isEnabled = false
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        } else {
            binding.resetPasswordEmail.error = null
            binding.btnResetPw.isEnabled = true
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
        }

    }
}
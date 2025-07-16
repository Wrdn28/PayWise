package com.utb.paywise

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.utb.paywise.databinding.ActivityAddTransactionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val db = FirebaseFirestore.getInstance()
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
        binding.edittextTransDate.setOnClickListener {
            showMaterialDatePicker()
        }

    }

    private fun saveTransaction() {
        val title = binding.edittextTransTitle.text.toString().trim()
        val amountText = binding.edittextTransAmount.text.toString().trim()
        val isIncome = binding.rbIncome.isChecked
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (title.isEmpty() || amountText.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toIntOrNull()
        if (amount == null) {
            Toast.makeText(this, "Jumlah harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = hashMapOf(
            "uid" to uid,
            "title" to title,
            "amount" to amount,
            "isIncome" to isIncome,
            "timestamp" to FieldValue.serverTimestamp(),
            "date" to selectedDate
        )

        db.collection("transactions").add(transaction)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showMaterialDatePicker() {
        val datePicker =
            com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih tanggal transaksi")
                .build()

        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            selectedDate = date

            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.edittextTransDate.setText(formatter.format(date))
        }
    }
}

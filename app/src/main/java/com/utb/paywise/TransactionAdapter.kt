package com.utb.paywise

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.utb.paywise.databinding.DialogEditTransactionBinding
import com.utb.paywise.databinding.ItemTransactionBinding
import com.utb.paywise.util.formatRupiah

class TransactionAdapter(private val list: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = list[position]
        holder.binding.tvTitle.text = transaction.title
        holder.binding.tvAmount.text = if (transaction.isIncome) {
            "+${formatRupiah(transaction.amount)}"
        } else {
            "-${formatRupiah(transaction.amount)}"
        }

        holder.binding.tvAmount.setTextColor(
            holder.itemView.context.getColor(
                if (transaction.isIncome) R.color.primary else R.color.red
            )
        )

        holder.binding.btnEdit.setOnClickListener {
            showEditDialog(transaction, holder.itemView.context)
        }

        holder.binding.btnDelete.setOnClickListener {
            deleteTransaction(transaction.id, holder.itemView.context)
        }
    }

    private fun showOptionsDialog(transaction: Transaction, context: Context) {
        val options = arrayOf("Edit", "Hapus")

        AlertDialog.Builder(context)
            .setTitle("Pilih Aksi")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(transaction, context)
                    1 -> deleteTransaction(transaction.id, context)
                }
            }
            .show()
    }

    private fun deleteTransaction(id: String, context: Context) {
        FirebaseFirestore.getInstance()
            .collection("transactions")
            .document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Transaksi dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditDialog(transaction: Transaction, context: Context) {
            val dialogBinding = DialogEditTransactionBinding.inflate(LayoutInflater.from(context))

            dialogBinding.etEditTitle.setText(transaction.title)
            dialogBinding.etEditAmount.setText(transaction.amount.toString())

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()

            dialogBinding.btnCancelEdit.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnSaveEdit.setOnClickListener {
                val newTitle = dialogBinding.etEditTitle.text.toString().trim()
                val newAmount = dialogBinding.etEditAmount.text.toString().toIntOrNull()

                if (newTitle.isEmpty() || newAmount == null) {
                    Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                updateTransaction(transaction.id, newTitle, newAmount, context)
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }



    private fun updateTransaction(id: String, newTitle: String, newAmount: Int, context: Context) {
        FirebaseFirestore.getInstance()
            .collection("transactions")
            .document(id)
            .update(mapOf(
                "title" to newTitle,
                "amount" to newAmount
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Transaksi diperbarui", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal memperbarui", Toast.LENGTH_SHORT).show()
            }
        }

    override fun getItemCount(): Int = list.size
}

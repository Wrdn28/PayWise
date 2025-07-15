package com.utb.paywise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.utb.paywise.databinding.FragmentExpenseBinding

class ExpenseFragment : Fragment() {

    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!

    private val transactions = mutableListOf<Transaction>()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TransactionAdapter(transactions)
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("transactions")
            .whereEqualTo("uid", uid)
            .whereEqualTo("isIncome", false)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                transactions.clear()
                snapshot?.forEach {
                    val id = it.id
                    val title = it.getString("title") ?: ""
                    val amount = it.getLong("amount")?.toInt() ?: 0
                    val isIncome = it.getBoolean("isIncome") ?: false
                    transactions.add(Transaction(id, title, amount, isIncome))
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

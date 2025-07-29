package com.utb.paywise

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.utb.paywise.databinding.ActivityMainBinding
import com.utb.paywise.util.formatRupiah

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val adapter = DashboardPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Income"
                    tab.setIcon(R.drawable.ic_income)
                }
                1 -> {
                    tab.text = "Expense"
                    tab.setIcon(R.drawable.ic_expense)
                }
            }

        }.attach()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        setupChart()
        setSupportActionBar(binding.toolbar)
    }

    private fun setupChart() {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("uid", uid)
            .addSnapshotListener { snapshot, _ ->
                var totalIncome = 0
                var totalExpense = 0

                snapshot?.forEach { doc ->
                    val isIncome = doc.getBoolean("isIncome") ?: false
                    val amount = doc.getLong("amount")?.toInt() ?: 0
                    if (isIncome) totalIncome += amount else totalExpense += amount
                }

                val entries = listOf(
                    PieEntry(totalIncome.toFloat(), "Income"),
                    PieEntry(totalExpense.toFloat(), "Expense")
                )

                val dataSet = PieDataSet(entries, "")
                dataSet.colors = listOf(
                    ContextCompat.getColor(this, R.color.primary),
                    ContextCompat.getColor(this, R.color.red)
                )

                val data = PieData(dataSet)
                dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)
                dataSet.valueTextSize = 14f

                // Convert to Rupiah
                data.setValueFormatter(object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return formatRupiah(value.toInt())
                    }
                })

                with(binding.pieChart) {
                    this.data = data
                    description.isEnabled = false
                    centerText = "Total"
                    setEntryLabelColor(Color.BLACK)
                    animateY(1000)
                    invalidate()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

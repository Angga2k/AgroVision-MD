package com.dicoding.agrovision.ui.view.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.HistoryViewModelFactory
import com.dicoding.agrovision.PredictionViewModelFactory
import com.dicoding.agrovision.data.local.UserPreference
import com.dicoding.agrovision.data.repository.PredictionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var userPreference: UserPreference
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoHistory: TextView

    // Fungsi untuk mengonversi waktu dari UTC ke UTC+7 (WIB)
    private fun convertToUTC7(dateString: String): String {
        // Format waktu dalam GMT (contoh: "Fri, 13 Dec 2024 03:28:12 GMT")
        val gmtFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        gmtFormat.timeZone = TimeZone.getTimeZone("GMT") // Set timezone ke GMT
        val date: Date = gmtFormat.parse(dateString) ?: return ""

        // Konversi waktu ke UTC+7 (WIB)
        val utc7Format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Format output yang diinginkan
        utc7Format.timeZone = TimeZone.getTimeZone("Asia/Jakarta") // Set timezone ke Jakarta (UTC+7)

        return utc7Format.format(date)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HistoryFragment", "Fragment onCreateView")

        // Inflasi layout fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Bind views
        recyclerView = view.findViewById(R.id.rvHistory)
        progressBar = view.findViewById(R.id.progressBar)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(emptyList())
        recyclerView.adapter = historyAdapter

        userPreference = UserPreference(requireContext())
        val repository = PredictionRepository()
        val factory = HistoryViewModelFactory(repository)  // Ganti dengan factory History
        historyViewModel = ViewModelProvider(this, factory).get(HistoryViewModel::class.java)

        // Ambil token dan load data
        lifecycleScope.launch {
            val token = userPreference.getToken().first()
            if (!token.isNullOrEmpty()) {
                showLoading(true)
                historyViewModel.getPredictionHistory(token)
            } else {
                showNoHistoryMessage(true)
                showLoading(false)
                Toast.makeText(requireContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        // Observasi data history
        historyViewModel.predictionHistory.observe(viewLifecycleOwner) { history ->
            Log.d("HistoryFragment", "Received history: $history")
            if (history.isNotEmpty()) {
                showLoading(false)
                showNoHistoryMessage(false)

                // Konversi waktu untuk setiap item dalam riwayat
                val formattedHistory = history.map {
                    it.copy(date = convertToUTC7(it.date))  // Asumsi 'time' adalah field yang berisi waktu
                }

                historyAdapter.updateData(formattedHistory)
            } else {
                showLoading(false)
                showNoHistoryMessage(true)
            }
        }

        // Observasi scan baru
        historyViewModel.newScan.observe(viewLifecycleOwner) { _ ->
            lifecycleScope.launch {
                val token = userPreference.getToken().first()
                if (!token.isNullOrEmpty()) {
                    historyViewModel.getPredictionHistory(token)  // Fetch updated history after new scan
                }
            }
        }

        return view // Kembalikan tampilan fragment yang sudah diinflasi
    }

    // Fungsi untuk menampilkan atau menyembunyikan ProgressBar
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    // Fungsi untuk menampilkan atau menyembunyikan pesan "Tidak ada riwayat"
    private fun showNoHistoryMessage(isEmpty: Boolean) {
        // Menampilkan pesan jika tidak ada riwayat

    }
}






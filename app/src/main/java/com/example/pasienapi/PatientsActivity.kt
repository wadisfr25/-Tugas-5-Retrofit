package com.example.pasienapi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pasienapi.adapter.PatientAdapter
import com.example.pasienapi.databinding.ActivityPatientsBinding
import com.example.pasienapi.model.ApiErrorResponse
import com.example.pasienapi.network.ApiClient
import com.example.pasienapi.session.SessionManager
import com.google.gson.Gson
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class PatientsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientsBinding
    private lateinit var sessionManager: SessionManager
    private val patientAdapter = PatientAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        sessionManager = SessionManager(this)
        val token = sessionManager.getToken()
        if (token.isNullOrBlank()) {
            redirectToLogin()
            return
        }

        setupRecyclerView()
        binding.retryButton.setOnClickListener { loadPatients(token) }
        binding.logoutButton.setOnClickListener { showLogoutConfirmation() }

        loadPatients(token)
    }

    private fun setupRecyclerView() {
        binding.patientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PatientsActivity)
            adapter = patientAdapter
        }
    }

    private fun loadPatients(token: String) {
        setState(isLoading = true, isError = false, isEmpty = false)
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getPatients("Bearer $token")
                if (response.isSuccessful) {
                    val patients = response.body()?.data.orEmpty()
                    patientAdapter.submitList(patients)
                    setState(
                        isLoading = false,
                        isError = false,
                        isEmpty = patients.isEmpty()
                    )
                } else {
                    if (response.code() == 401) {
                        sessionManager.clearToken()
                        Toast.makeText(
                            this@PatientsActivity,
                            getString(R.string.session_expired),
                            Toast.LENGTH_SHORT
                        ).show()
                        redirectToLogin()
                        return@launch
                    }

                    showError(parseErrorMessage(response.errorBody()?.string()))
                }
            } catch (_: Exception) {
                showError(getString(R.string.network_error))
            }
        }
    }

    private fun parseErrorMessage(rawError: String?): String {
        if (rawError.isNullOrBlank()) return getString(R.string.generic_error)
        return runCatching {
            Gson().fromJson(rawError, ApiErrorResponse::class.java).message
        }.getOrNull().orEmpty().ifBlank { getString(R.string.generic_error) }
    }

    private fun showError(message: String) {
        patientAdapter.submitList(emptyList())
        binding.errorTextView.text = message
        setState(isLoading = false, isError = true, isEmpty = false)
    }

    private fun setState(isLoading: Boolean, isError: Boolean, isEmpty: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.patientsRecyclerView.visibility =
            if (!isLoading && !isError && !isEmpty) View.VISIBLE else View.GONE
        binding.errorGroup.visibility = if (isError) View.VISIBLE else View.GONE
        binding.emptyTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun applyWindowInsets() {
        val toolbarInitialTop = binding.toolbar.paddingTop
        val recyclerInitialBottom = binding.patientsRecyclerView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = toolbarInitialTop + systemBars.top)
            binding.patientsRecyclerView.updatePadding(bottom = recyclerInitialBottom + systemBars.bottom)
            insets
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                sessionManager.clearToken()
                redirectToLogin()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

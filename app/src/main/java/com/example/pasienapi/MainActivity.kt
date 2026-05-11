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
import com.example.pasienapi.databinding.ActivityMainBinding
import com.example.pasienapi.model.ApiErrorResponse
import com.example.pasienapi.model.LoginRequest
import com.example.pasienapi.network.ApiClient
import com.example.pasienapi.session.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        sessionManager = SessionManager(this)
        if (sessionManager.getToken() != null) {
            openPatientsScreen()
            return
        }

        binding.loginButton.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {
        val email = binding.emailEditText.text?.toString()?.trim().orEmpty()
        val password = binding.passwordEditText.text?.toString().orEmpty()

        binding.emailLayout.error = null
        binding.passwordLayout.error = null

        var hasError = false
        if (email.isBlank()) {
            binding.emailLayout.error = getString(R.string.email_required)
            hasError = true
        }
        if (password.isBlank()) {
            binding.passwordLayout.error = getString(R.string.password_required)
            hasError = true
        }
        if (hasError) return

        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.data?.token
                    if (!token.isNullOrBlank()) {
                        sessionManager.saveToken(token)
                        Toast.makeText(
                            this@MainActivity,
                            body.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        openPatientsScreen()
                    } else {
                        showMessage(getString(R.string.generic_error))
                    }
                } else {
                    showMessage(parseErrorMessage(response.errorBody()?.string()))
                }
            } catch (_: Exception) {
                showMessage(getString(R.string.network_error))
            } finally {
                setLoading(false)
            }
        }
    }

    private fun parseErrorMessage(rawError: String?): String {
        if (rawError.isNullOrBlank()) return getString(R.string.generic_error)
        return runCatching {
            Gson().fromJson(rawError, ApiErrorResponse::class.java).message
        }.getOrNull().orEmpty().ifBlank { getString(R.string.generic_error) }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
        binding.loginProgressIndicator.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun applyWindowInsets() {
        val initialTop = binding.root.paddingTop
        val initialBottom = binding.root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = initialTop + systemBars.top,
                bottom = initialBottom + systemBars.bottom
            )
            insets
        }
    }

    private fun openPatientsScreen() {
        startActivity(Intent(this, PatientsActivity::class.java))
        finish()
    }
}

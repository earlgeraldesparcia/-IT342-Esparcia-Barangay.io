package edu.cit.esparcia.barangayio.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.cit.esparcia.barangayio.mobile.api.LoginRequest
import edu.cit.esparcia.barangayio.mobile.api.RetrofitClient
import edu.cit.esparcia.barangayio.mobile.databinding.LoginBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val sharedPref = getSharedPreferences("BarangayIO_Prefs", MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", "")
        val role = sharedPref.getString("ROLE", "resident")
        if (!userId.isNullOrEmpty()) {
            if (role == "admin" || role == "ADMIN") {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
            } else {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            finish()
            return
        }

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.loginUser(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val sharedPref = getSharedPreferences("BarangayIO_Prefs", MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString("USER_ID", loginResponse.userId)
                            putString("FIRST_NAME", loginResponse.firstName)
                            putString("LAST_NAME", loginResponse.lastName)
                            putString("ROLE", loginResponse.role)
                            apply()
                        }
                    }
                    
                    Toast.makeText(this@LoginActivity, loginResponse?.message ?: "Login Successful", Toast.LENGTH_LONG).show()
                    
                    if (loginResponse?.role?.lowercase() == "admin") {
                        startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    }
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Invalid email or password"
                    }
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Connection Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

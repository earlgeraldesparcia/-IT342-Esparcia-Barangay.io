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
                    Toast.makeText(this@LoginActivity, loginResponse?.message ?: "Login Successful", Toast.LENGTH_LONG).show()
                    
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
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

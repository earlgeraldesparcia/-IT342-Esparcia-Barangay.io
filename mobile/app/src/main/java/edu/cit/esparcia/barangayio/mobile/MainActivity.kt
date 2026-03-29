package edu.cit.esparcia.barangayio.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.cit.esparcia.barangayio.mobile.api.RegisterRequest
import edu.cit.esparcia.barangayio.mobile.api.RetrofitClient
import edu.cit.esparcia.barangayio.mobile.databinding.RegisterBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: RegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validate(firstName, lastName, email, password, phone, address)) {
                register(firstName, lastName, email, password, phone, address)
            } else {
                Toast.makeText(this, "Please check your inputs", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun register(firstName: String, lastName: String, email: String, password: String, phone: String, address: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.registerUser(
                    RegisterRequest(firstName, lastName, email, password, phone, address)
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, response.body()?.message ?: "Success", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Registration failed"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Connection Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validate(firstName: String, lastName: String, email: String, password: String, phone: String, address: String): Boolean {
        return firstName.isNotBlank() &&
               lastName.isNotBlank() &&
               email.contains("@") &&
               password.length >= 8 &&
               phone.isNotBlank() &&
               address.isNotBlank()
    }
}

package com.kepavi.eduseed
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SecondActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        messageEditText = findViewById(R.id.messageEditText)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val message = messageEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && message.isNotEmpty()) {
                sendDataToGoogleSheets(name, email, message)
            } else {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sendDataToGoogleSheets(name: String, email: String, message: String) {
        val url = "https://script.google.com/macros/s/AKfycbwTGFJO-zggr8Dl4bsPi5VKQO6uzmE4Aue1bSCaJYhFyyjdp8bNicLqrfDNI7CzDkhD/exec"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                // Proper JSON string formatting
                val jsonPayload = "{\"name\":\"$name\", \"email\":\"$email\", \"message\":\"$message\"}"

                // Send the payload as a string
                connection.outputStream.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(jsonPayload)
                        writer.flush()
                    }
                }

                // Get the response code
                val responseCode = connection.responseCode
                Log.d("Response Code", responseCode.toString())

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this@SecondActivity, "Data submitted successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SecondActivity, "Failed to submit data. Response code: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SecondActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}

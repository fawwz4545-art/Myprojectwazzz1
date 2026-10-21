package com.example.myprojectwazzz

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprojectwazzz.ui.theme.MyProjectWazzzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyProjectWazzzTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FormulirBeasiswaScreen()
                }
            }
        }
    }
}

@Composable
fun FormulirBeasiswaScreen() {
    // STATE MANAGEMENT: Menggunakan rememberSaveable sebagai pengganti onSaveInstanceState.
    // Ini mencegah 'The Disappearing Data Problem' (State Loss) saat terjadi rotasi layar ponsel.
    var nama by rememberSaveable { mutableStateOf("") }
    var nim by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var ipk by rememberSaveable { mutableStateOf("") }

    // State untuk menyimpan pesan error spesifik
    var namaError by rememberSaveable { mutableStateOf<String?>(null) }
    var nimError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var ipkError by rememberSaveable { mutableStateOf<String?>(null) }

    var statusMessage by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Formulir Pendaftaran Beasiswa",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider()

        // INPUT: Nama
        OutlinedTextField(
            value = nama,
            onValueChange = {
                nama = it
                namaError = null // Instant feedback (doAfterTextChanged)
            },
            label = { Text("Nama Lengkap") },
            isError = namaError != null,
            supportingText = { namaError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // INPUT: NIM (8-10 digit)
        OutlinedTextField(
            value = nim,
            onValueChange = {
                nim = it
                nimError = null
            },
            label = { Text("NIM") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = nimError != null,
            supportingText = { nimError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // INPUT: Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email Mahasiswa") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // INPUT: IPK (Double 0.00-4.00)
        OutlinedTextField(
            value = ipk,
            onValueChange = {
                ipk = it
                ipkError = null
            },
            label = { Text("IPK") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = ipkError != null,
            supportingText = { ipkError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                var isValid = true

                // Validasi Nama
                if (nama.isBlank()) {
                    namaError = "Nama wajib diisi!"
                    isValid = false
                }

                // Validasi NIM: Panjang karakter 8-10 digit
                if (nim.length !in 8..10) {
                    nimError = "NIM harus berjumlah 8 hingga 10 digit!"
                    isValid = false
                }

                // Validasi Email: Menggunakan Patterns.EMAIL_ADDRESS
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Format email tidak valid!"
                    isValid = false
                }

                // Validasi IPK: Konversi angka aman menggunakan toDoubleOrNull()
                val ipkValue = ipk.toDoubleOrNull()
                if (ipkValue == null || ipkValue < 0.0 || ipkValue > 4.0) {
                    ipkError = "IPK harus berupa angka antara 0.00 - 4.00!"
                    isValid = false
                }

                // Jika semua validasi lolos
                statusMessage = if (isValid) {
                    "Pendaftaran Berhasil Disimpan!\n\nNama: $nama\nNIM: $nim\nEmail: $email\nIPK: $ipkValue"
                } else {
                    ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Pendaftaran")
        }

        // Output Hasil
        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Preview Formulir Beasiswa")
@Composable
fun FormulirBeasiswaScreenPreview() {
    MyProjectWazzzTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FormulirBeasiswaScreen()
        }
    }
}

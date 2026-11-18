package com.example.myprojectwazzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myprojectwazzz.network.ApiClient
import com.example.myprojectwazzz.network.UserItem
import com.example.myprojectwazzz.ui.theme.MyProjectWazzzTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException

// --- 1. VIEWMODEL (Mengelola HTTP Request di Background Thread) ---
class UserViewModel : ViewModel() {
    var userList by mutableStateOf<List<UserItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var postResultMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchUsers()
    }

    // Operasi HTTP GET
    fun fetchUsers(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                val response = ApiClient.instance.getListUsers(page)
                if (response.isSuccessful) {
                    userList = response.body()?.users ?: emptyList()
                } else {
                    errorMessage = "Gagal memuat data (HTTP ${response.code()})"
                }
            } catch (e: UnknownHostException) {
                errorMessage = "Tidak ada koneksi internet atau server tidak terjangkau."
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Operasi HTTP POST
    fun addUser(firstName: String, lastName: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            postResultMessage = null
            try {
                val newUser = UserItem(
                    firstName = firstName,
                    lastName = lastName,
                    email = email
                )
                val response = ApiClient.instance.createNewUser(newUser)
                if (response.isSuccessful) {
                    val created = response.body()
                    postResultMessage = "Berhasil POST (HTTP ${response.code()}): ${created?.firstName} ${created?.lastName} (ID: ${created?.id})"
                } else {
                    postResultMessage = "Gagal mengirim data (HTTP ${response.code()})"
                }
            } catch (e: Exception) {
                postResultMessage = "Error POST: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}

// --- 2. MAIN ACTIVITY ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyProjectWazzzTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RetrofitScreen()
                }
            }
        }
    }
}

// --- 3. STATEFUL COMPOSABLE ---
@Composable
fun RetrofitScreen(viewModel: UserViewModel = viewModel()) {
    RetrofitContent(
        userList = viewModel.userList,
        isLoading = viewModel.isLoading,
        errorMessage = viewModel.errorMessage,
        postResultMessage = viewModel.postResultMessage,
        onRefresh = { viewModel.fetchUsers() },
        onCreateUser = { firstName, lastName, email ->
            viewModel.addUser(firstName, lastName, email)
        }
    )
}

// --- 4. STATELESS COMPOSABLE (UI Murni) ---
@Composable
fun RetrofitContent(
    userList: List<UserItem>,
    isLoading: Boolean,
    errorMessage: String?,
    postResultMessage: String?,
    onRefresh: () -> Unit,
    onCreateUser: (String, String, String) -> Unit
) {
    var firstNameInput by rememberSaveable { mutableStateOf("") }
    var lastNameInput by rememberSaveable { mutableStateOf("") }
    var emailInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Pertemuan 9: REST API & Retrofit",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider()

        // Form Simulasi HTTP POST
        Text(text = "Tambah User Baru (HTTP POST)", fontWeight = FontWeight.SemiBold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = firstNameInput,
                onValueChange = { firstNameInput = it },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = lastNameInput,
                onValueChange = { lastNameInput = it },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (firstNameInput.isNotBlank() && emailInput.isNotBlank()) {
                        onCreateUser(firstNameInput, lastNameInput, emailInput)
                        firstNameInput = ""
                        lastNameInput = ""
                        emailInput = ""
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Kirim (POST)")
            }

            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier.weight(1f)
            ) {
                Text("Muat Ulang (GET)")
            }
        }

        // Notifikasi Hasil HTTP POST
        postResultMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        HorizontalDivider()
        Text(text = "Daftar User dari Server (HTTP GET):", fontWeight = FontWeight.SemiBold)

        // Indikator Loading, Error, atau Daftar Data JSON
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(userList) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "${user.firstName} ${user.lastName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = user.email,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 5. PREVIEW COMPOSABLE ---
@Preview(showBackground = true, name = "Preview REST API Retrofit")
@Composable
fun RetrofitScreenPreview() {
    MyProjectWazzzTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RetrofitContent(
                userList = listOf(
                    UserItem(id = 1, firstName = "George", lastName = "Bluth", email = "george.bluth@reqres.in"),
                    UserItem(id = 2, firstName = "Janet", lastName = "Weaver", email = "janet.weaver@reqres.in")
                ),
                isLoading = false,
                errorMessage = null,
                postResultMessage = "Berhasil POST (HTTP 201): Budi Santoso (ID: 405)",
                onRefresh = {},
                onCreateUser = { _, _, _ -> }
            )
        }
    }
}

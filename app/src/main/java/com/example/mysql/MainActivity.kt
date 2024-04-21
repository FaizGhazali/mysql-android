package com.example.mysql

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mysql.ui.theme.MysqlTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MysqlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserData()
                }
            }
        }
    }
}

@Composable
fun UserData() {
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val scope = rememberCoroutineScope()

        Button(
            onClick = {
                scope.launch {
                    username = fetchDataFromAPI()
                }
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Fetch Username")
        }
        Text(text = username)
    }
}

suspend fun fetchDataFromAPI(): String {
    return try {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.1.103:3000/users")
            .build()

        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }

        val responseData = response.body?.string() ?: "Error fetching data"

        // Assuming responseData is in JSON format
        val jsonArray = JSONArray(responseData)
        val names = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            names.add(name)
        }

        // Join the names with a comma and return
        names.joinToString(", ")
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

@Preview(showBackground = true)
@Composable
fun UserDataPreview() {
    MysqlTheme {
        UserData()
    }
}

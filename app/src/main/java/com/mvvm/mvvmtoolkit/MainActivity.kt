package com.mvvm.mvvmtoolkit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModelClass
    private val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val postLiveDataKey = "apiPostResponseKey"
        val getLiveDataKey = "apiGetResponseKey"
        val multipartLiveDataKey = "apiMultipartResponseKey"

        viewModel = ViewModelProvider(
            this,
            ViewModelClassFactory(repository)
        )[ViewModelClass::class.java]

        viewModel.getLiveData<String>(postLiveDataKey).observe(this) { value ->
            // Handle the response or error
            println("$postLiveDataKey -> $value")
        }

        viewModel.getLiveData<String>(getLiveDataKey).observe(this) { value ->
            // Handle the response or error
            println("$getLiveDataKey -> $value")
        }

        viewModel.getLiveData<String>(multipartLiveDataKey).observe(this) { value ->
            // Handle the response or error
            println("$multipartLiveDataKey -> $value")
        }

        viewModel.callPostApi(
            "https://example.com/api",
            JSONObject().apply {
                put("key1", "value1")
                put("key2", "value2")
            }.toString(),
            postLiveDataKey
        )

        viewModel.callGetApi("https://example.com/api", getLiveDataKey)

        val files = mapOf("file1" to File("/path/to/file1.jpg"))
        val formData = mapOf("key1" to "value1", "key2" to "value2")
        val apiUrl = "https://example.com/upload"

        viewModel.callMultipartApi(apiUrl, files, formData, multipartLiveDataKey)
    }
}
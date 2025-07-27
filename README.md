# MVVMToolkit

**A lightweight, Retrofit-free Android MVVM toolkit for seamless API integration using LiveData, ViewModel, and Coroutines.**

MVVMToolkit helps you set up network communication using the MVVM architecture without needing Retrofit or boilerplate code. Simply initialize the toolkit and observe LiveData responses with minimal setup.

---

## ✨ Features

- 🔧 No Retrofit required
- 🧠 Uses Android Jetpack: ViewModel + LiveData + Coroutines
- 📦 Plug-and-play architecture for quick API consumption
- 📤 Supports Multipart Upload with progress tracking
- 📃 Clean API for JSON object and array responses
- 🔄 Dynamic model registration
- ✅ Lightweight and easy to integrate

---

## 📦 Installation

#### 1. Add the dependency

<pre>dependencies {
    implementation(files("libs/mvvmtoolkit-release.aar"))
}</pre>
---

## 🚀 Quick Start

### 1. Initialize the toolkit

<pre>package com.nova.mvvmtoolkittest.application

import android.app.Application
import com.google.gson.reflect.TypeToken
import com.nova.mvvmtoolkit.api.MVVMToolkit
import com.nova.mvvmtoolkittest.model.PhotosModel

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        MVVMToolkit.init(
            "https://jsonplaceholder.typicode.com/",
            mapOf("photos" to PhotosModel::class.java)
        )
    }
}
</pre>

### 2. Create your data model

<pre>package com.nova.mvvmtoolkittest.model

data class PhotosModel(
    val albumId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val url: String = "",
    val thumbnailUrl: String = ""
)
</pre>

### 3. Observe API response

<pre>package com.nova.mvvmtoolkittest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nova.mvvmtoolkit.api.MVVMToolkit
import com.nova.mvvmtoolkittest.model.PhotosModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MVVMToolkit.observeApi<PhotosModel>(owner = this,
            method = "GET", endpoint = "photos", modelName = "photos", requestBody = null) {
            result ->

            result.onSuccess { data ->
                Log.e("printData", data.toString())
            }.onFailure { error ->
                    Log.e("printError", error.toString())
                }
        }
    }
}</pre>

### 📤 Multipart Upload (with progress)

<pre>val files = listOf("image" to File("/path/to/image.jpg"))
val fields = mapOf("user_id" to "123")

MVVMToolkit.uploadMultipart(
    lifecycleOwner = this,
    endpoint = "upload",
    files = files,
    formFields = fields,
    isArray = false,
    onProgress = { progress ->
        Log.d("UploadProgress", "Progress: $progress%")
    }
) { result ->
    when (result) {
        is Result.Success -> { /* handle success */ }
        is Result.Error -> { /* handle error */ }
    }
}
</pre>

---

## 🛠️ ProGuard Rules
If you're using ProGuard or R8, add this:

<pre>-keep class com.nova.mvvmtoolkit.** { *; }
-keep class * extends androidx.lifecycle.ViewModel
</pre>

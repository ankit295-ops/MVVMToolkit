package com.mvvm.mvvmtoolkit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class ViewModelClass(private val repository: Repository) : ViewModel() {

    private val liveDataMap: MutableMap<String, MutableLiveData<Any>> = mutableMapOf()

    fun <T> getLiveData(key: String): MutableLiveData<T> {
        @Suppress("UNCHECKED_CAST")
        return liveDataMap.getOrPut(key) { MutableLiveData() } as MutableLiveData<T>
    }

    fun callPostApi(apiUrl: String, requestBody: String, liveDataKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.postApiCall(apiUrl, requestBody)
                val liveData = getLiveData<String>(liveDataKey)
                liveData.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
                val liveData = getLiveData<String>(liveDataKey)
                liveData.postValue("Error: ${e.message}")
            }
        }
    }

    fun callGetApi(apiUrl: String, liveDataKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.makeGetCall(apiUrl)
                val liveData = getLiveData<String>(liveDataKey)
                liveData.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
                val liveData = getLiveData<String>(liveDataKey)
                liveData.postValue("Error: ${e.message}")
            }
        }
    }

    fun callMultipartApi(
        apiUrl: String,
        files: Map<String, File>,
        formData: Map<String, String>,
        liveDataKey: String
    ) {
        viewModelScope.launch {
            try {
                val response = repository.makeMultipartPostCall(apiUrl, files, formData)
                val liveData = getLiveData<String>(liveDataKey)
                liveData.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
                val liveData = getLiveData<String>(liveDataKey)
                liveData.postValue("Error: ${e.message}")
            }
        }
    }
}

class ViewModelClassFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelClass(repository) as T
    }
}
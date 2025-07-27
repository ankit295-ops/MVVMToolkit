package com.nova.mvvmtoolkit.api

import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.nova.mvvmtoolkit.core.ModelRegistry
import com.nova.mvvmtoolkit.core.ModelViewModel
import com.nova.mvvmtoolkit.core.ToolkitViewModelFactory
import java.io.File

/**
 * A singleton entry point to initialize and use the MVVM Toolkit for API handling.
 */
@Keep
object MVVMToolkit {

    /**
     * Initializes the toolkit with the base URL and a map of model names to their corresponding classes.
     *
     * @param baseUrl The base URL to be used for API calls.
     * @param mapModel A map containing model names as keys and their corresponding class types as values.
     */
    @Keep
    fun init(baseUrl: String, mapModel: Map<String, Class<out Any>>) {
        ModelRegistry.init(baseUrl, mapModel)
    }

    /**
     * Returns a ViewModel instance responsible for API communication.
     *
     * @param owner The owner of the ViewModelStore (Activity or Fragment).
     * @param modelClass Optional model class to associate with the ViewModel.
     * @return [ModelViewModel] instance.
     */
    private fun <T : Any> getModelViewModel(
        owner: ViewModelStoreOwner,
        modelClass: Class<T>?
    ): ModelViewModel {
        val factory = if (modelClass != null) {
            ToolkitViewModelFactory(modelClass)
        } else {
            ToolkitViewModelFactory()
        }

        return ViewModelProvider(owner, factory)[ModelViewModel::class.java]
    }

    /**
     * Observes a normal API call and provides result through a callback.
     *
     * @param owner The lifecycle and ViewModelStore owner (Activity or Fragment).
     * @param method HTTP method (GET, POST, etc).
     * @param endpoint API endpoint relative to base URL.
     * @param modelName Optional model name as a key to retrieve its class.
     * @param requestBody Optional request body object for POST/PUT requests.
     * @param onResult Callback with the result of the API call.
     */
    @Keep
    fun <T : Any> observeApi(
        owner: ViewModelStoreOwner,
        method: String,
        endpoint: String,
        modelName: String? = null,
        requestBody: Any? = null,
        onResult: (Result<Any>) -> Unit
    ) {
        if (modelName != null) {
            val modelClass = modelName.let { ModelRegistry.getModelClass<T>(it) }


            val viewModel = getModelViewModel(owner, modelClass)

            viewModel.getLiveData(method, endpoint, requestBody)
                .observe(owner as LifecycleOwner) { result ->
                    onResult(result)
                }
        } else {
            val viewModel = getModelViewModel<T>(owner, null)

            viewModel.getLiveData(method, endpoint, requestBody)
                .observe(owner as LifecycleOwner) { result ->
                    onResult(result)
                }
        }
    }

    /**
     * Observes a multipart API call and provides result and progress updates through callbacks.
     *
     * @param owner The lifecycle and ViewModelStore owner (Activity or Fragment).
     * @param endpoint API endpoint relative to base URL.
     * @param modelName Optional model name as a key to retrieve its class.
     * @param files A list of file key-value pairs where key is the field name and value is the [File] to upload.
     * @param formFields Optional map of additional form fields.
     * @param onProgress Optional callback for tracking upload progress.
     * @param onResult Callback with the result of the API call.
     */
    @Keep
    fun <T : Any> observeMultipartApi(
        owner: ViewModelStoreOwner,
        endpoint: String,
        modelName: String? = null,
        files: List<Pair<String, File>>,
        formFields: Map<String, String> = emptyMap(),
        onProgress: ((Int) -> Unit)? = null,
        onResult: (Result<Any>) -> Unit
    ) {
        if (modelName != null) {
            val modelClass = modelName.let { ModelRegistry.getModelClass<T>(it) }

            val viewModel = getModelViewModel(owner, modelClass)

            viewModel.getMultipartLiveData(
                endpoint,
                files,
                formFields,
                onProgress
            ).observe(owner as LifecycleOwner) {
                onResult(it)
            }
        }
    }
}
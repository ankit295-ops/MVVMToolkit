package com.mvvm.mvvmtoolkit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class Repository {

    suspend fun postApiCall(apiUrl: String, requestBody: String): String {
        var response = ""
        var connection: HttpURLConnection? = null
        try {
            val url = URL(apiUrl)
            connection = withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.doInput = true

            val outputStream = BufferedOutputStream(connection.outputStream)
            withContext(Dispatchers.IO) {
                outputStream.write(requestBody.toByteArray())
                outputStream.flush()
            }

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                response = reader.readText()
                withContext(Dispatchers.IO) {
                    reader.close()
                }
            } else {
                throw Exception("HTTP error code: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            response = e.message ?: "Error occurred"
        } finally {
            connection?.disconnect()
        }

        return response
    }

    suspend fun makeGetCall(apiUrl: String): String {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                // Open a connection to the URL
                val url = URL(apiUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    response.toString()
                } else {
                    throw Exception("HTTP error code: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            } finally {
                connection?.disconnect()
            }
        }
    }

    suspend fun makeMultipartPostCall(
        apiUrl: String,
        files: Map<String, File>,
        formData: Map<String, String>
    ): String {
        return withContext(Dispatchers.IO) {
            val boundary = "Boundary-${System.currentTimeMillis()}"
            var connection: HttpURLConnection? = null
            try {
                // Open connection
                val url = URL(apiUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "POST"
                    doInput = true
                    doOutput = true
                    useCaches = false
                    setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                }

                // Write multipart data
                val outputStream = DataOutputStream(connection.outputStream)
                writeFormData(outputStream, boundary, formData)
                writeFileData(outputStream, boundary, files)
                outputStream.writeBytes("--$boundary--\r\n")
                outputStream.flush()
                outputStream.close()

                // Get response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    throw Exception("HTTP error code: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun writeFormData(
        outputStream: DataOutputStream,
        boundary: String,
        formData: Map<String, String>
    ) {
        for ((key, value) in formData) {
            outputStream.writeBytes("--$boundary\r\n")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
            outputStream.writeBytes("$value\r\n")
        }
    }

    private fun writeFileData(
        outputStream: DataOutputStream,
        boundary: String,
        files: Map<String, File>
    ) {
        for ((key, file) in files) {
            outputStream.writeBytes("--$boundary\r\n")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"; filename=\"${file.name}\"\r\n")
            outputStream.writeBytes("Content-Type: ${guessMimeType(file)}\r\n\r\n")

            val fileInputStream = FileInputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            fileInputStream.close()
            outputStream.writeBytes("\r\n")
        }
    }

    private fun guessMimeType(file: File): String {
        return URLConnection.guessContentTypeFromName(file.name) ?: "application/octet-stream"
    }
}
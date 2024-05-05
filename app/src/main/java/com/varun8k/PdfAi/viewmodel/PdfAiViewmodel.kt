package com.varun8k.PdfAi.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varun8k.PdfAi.api.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class PdfAiViewModel : ViewModel() {
    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> = _uploadStatus
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadFile(uri: Uri, applicationContext: Context,
                   contentResolver: ContentResolver
    ) {
        val fileDir = applicationContext.filesDir
        val file = File(fileDir, "pdf_file.pdf")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)
        _isLoading.value=true
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val filePart = MultipartBody.Part.createFormData("pdf_file", file.name, requestFile)
        viewModelScope.launch {
            try {
                 RetrofitClient.apiService.uploadFile(filePart)
                    _uploadStatus.value = true
                    _isLoading.value=false
            } catch (e: Exception) {
                Log.e("UploadFile", "Exception: ${e.message}", e)
                _uploadStatus.value = false
                _isLoading.value=false
            }
        }
    }
}
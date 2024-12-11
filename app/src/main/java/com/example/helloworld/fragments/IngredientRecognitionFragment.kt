package com.example.helloworld.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R
import com.example.helloworld.network.ApiResponse
import com.example.helloworld.network.ApiService
import com.example.helloworld.network.DetectedClass
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class IngredientRecognitionFragment : Fragment() {
    private lateinit var apiService: ApiService
    private lateinit var imageView: ImageView
    private lateinit var ingredientsResultTextView: TextView
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredient_recognition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.imageView)
        ingredientsResultTextView = view.findViewById(R.id.ingredientsResultText)
        val uploadButton = view.findViewById<Button>(R.id.uploadButton)

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // API 주소
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
                    .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃
                    .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                handleImageSelection(uri)
            }
        }
    }

    private fun handleImageSelection(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        imageView.setImageBitmap(bitmap)

        uploadImage(bitmap) { response ->
            response?.let {
                // allDetectedClasses 사용 (응답에서 탐지된 클래스 리스트 가져오기)
                val detectedClasses = it.allDetectedClasses?.joinToString("\n") { detectedClass ->
                    detectedClass.name ?: "Unknown"
                } ?: "No ingredients detected."

                ingredientsResultTextView.text = "Detected Ingredients:\n$detectedClasses"
            } ?: run {
                ingredientsResultTextView.text = "Failed to get detection result."
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap, callback: (ApiResponse<List<DetectedClass>>?) -> Unit) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val filePart = MultipartBody.Part.createFormData("file", "selected_image.jpg", requestBody)

        apiService.detectIngredients(filePart).enqueue(object : Callback<ApiResponse<List<DetectedClass>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<DetectedClass>>>,
                response: Response<ApiResponse<List<DetectedClass>>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("IngredientRecognition", "Response failed: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<DetectedClass>>>, t: Throwable) {
                Log.e("IngredientRecognition", "Error: ${t.message}")
                callback(null)
            }
        })
    }
}



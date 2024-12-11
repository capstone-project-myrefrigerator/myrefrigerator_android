/*
package com.example.helloworld

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.ByteArrayOutputStream

// API 인터페이스 정의
interface ApiService {
    @Multipart
    @POST("predict/")
    fun predictEmotion(@Part file: MultipartBody.Part): Call<ApiResponse>
}

// API 응답 데이터 클래스 정의
data class ApiResponse(
    val predicted_emotion: String,
    val error: String?
)

class MainActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // Retrofit 클라이언트 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // API 서버 주소
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        try {
            // drawable 폴더에서 이미지 로드
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)

            // 이미지를 API에 업로드
            uploadImage(bitmap) { response ->
                response?.let {
                    resultTextView.text = "Predicted Emotion: ${it.predicted_emotion}"
                } ?: run {
                    resultTextView.text = "Failed to get prediction."
                }
            }

            // 이미지 뷰에 표시
            imageView.setImageBitmap(bitmap)

        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading image or making API call", e)
            resultTextView.text = "Error loading image or making API call"
        }
    }

    private fun uploadImage(bitmap: Bitmap, callback: (ApiResponse?) -> Unit) {
        try {
            // Bitmap을 ByteArray로 변환
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // MultipartBody로 파일 생성
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
            val filePart = MultipartBody.Part.createFormData("file", "sample_image.jpg", requestBody)

            // Retrofit API 호출
            apiService.predictEmotion(filePart).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    } else {
                        Log.e("API", "Error: ${response.errorBody()?.string()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e("API", "Failure: ${t.message}")
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e("API", "Error in uploadImage", e)
            callback(null)
        }
    }
}
*/


//package com.example.helloworld
//
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.OkHttpClient
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.Multipart
//import retrofit2.http.POST
//import retrofit2.http.Part
//import java.io.ByteArrayOutputStream
//
//// API 인터페이스 정의
//interface ApiService {
//    @Multipart
//    @POST("predict/")
//    fun predictEmotion(@Part file: MultipartBody.Part): Call<ApiResponse>
//}
//
//// API 응답 데이터 클래스 정의
//data class ApiResponse(
//    val predicted_emotion: String,
//    val error: String?
//)
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var apiService: ApiService
//    private lateinit var imageView: ImageView
//    private lateinit var resultTextView: TextView
//    private val PICK_IMAGE_REQUEST = 1
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        imageView = findViewById(R.id.imageView)
//        resultTextView = findViewById(R.id.resultTextView)
//        val uploadButton = findViewById<Button>(R.id.uploadButton)
//
//        // Retrofit 클라이언트 초기화
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://10.0.2.2:8000/") // API 서버 주소
//            .client(OkHttpClient.Builder().build())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        apiService = retrofit.create(ApiService::class.java)
//
//        // 버튼 클릭 시 이미지 선택
//        uploadButton.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, PICK_IMAGE_REQUEST)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            data?.data?.let { uri ->
//                handleImageSelection(uri)
//            }
//        }
//    }
//
//    private fun handleImageSelection(uri: Uri) {
//        try {
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//            imageView.setImageBitmap(bitmap)
//            uploadImage(bitmap) { response ->
//                response?.let {
//                    resultTextView.text = "Predicted Emotion: ${it.predicted_emotion}"
//                } ?: run {
//                    resultTextView.text = "Failed to get prediction."
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("MainActivity", "Error processing image", e)
//            resultTextView.text = "Error processing image."
//        }
//    }
//
//    private fun uploadImage(bitmap: Bitmap, callback: (ApiResponse?) -> Unit) {
//        try {
//            // Bitmap을 ByteArray로 변환
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//            val byteArray = byteArrayOutputStream.toByteArray()
//
//            // MultipartBody로 파일 생성
//            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
//            val filePart = MultipartBody.Part.createFormData("file", "selected_image.jpg", requestBody)
//
//            // Retrofit API 호출
//            apiService.predictEmotion(filePart).enqueue(object : Callback<ApiResponse> {
//                override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
//                    if (response.isSuccessful) {
//                        callback(response.body())
//                    } else {
//                        Log.e("API", "Error: ${response.errorBody()?.string()}")
//                        callback(null)
//                    }
//                }
//
//                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                    Log.e("API", "Failure: ${t.message}")
//                    callback(null)
//                }
//            })
//        } catch (e: Exception) {
//            Log.e("API", "Error in uploadImage", e)
//            callback(null)
//        }
//    }
//}

package com.example.helloworld

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.fragment.app.Fragment
import com.example.helloworld.fragments.EmotionPredictionFragment
import com.example.helloworld.fragments.HomeFragment
import com.example.helloworld.fragments.IngredientRecognitionFragment
import com.example.helloworld.fragments.IngredientsFragment
import com.example.helloworld.fragments.MyProfileFragment
import com.example.helloworld.network.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 초기 화면 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> switchFragment(HomeFragment())
                R.id.nav_emotion_prediction -> switchFragment(EmotionPredictionFragment())
                R.id.nav_ingredient_recognition -> switchFragment(IngredientRecognitionFragment())
                R.id.nav_ingredients -> switchFragment(IngredientsFragment())
                R.id.nav_my_page -> switchFragment(MyProfileFragment())
            // 다른 프래그먼트 추가 가능
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

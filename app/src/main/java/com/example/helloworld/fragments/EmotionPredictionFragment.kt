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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.RecipeDetailActivity
import com.example.helloworld.adapters.RecipeAdapter
import com.example.helloworld.network.ApiResponse
import com.example.helloworld.network.ApiService
import com.example.helloworld.network.RecipeCategoryFilteringPreviewDTOList
import com.example.helloworld.network.RecipeDetailDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class EmotionPredictionFragment : Fragment() {
    private lateinit var apiService: ApiService
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var emotionMessageTextView: TextView
    private lateinit var recipeSearchButton: Button
    private lateinit var recipeRecyclerView: RecyclerView
    private val PICK_IMAGE_REQUEST = 1
    private var predictedEmotion: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_emotion_prediction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        resultTextView = view.findViewById(R.id.predicted_emotion)
        emotionMessageTextView = view.findViewById(R.id.emotionMessage)
        recipeSearchButton = view.findViewById(R.id.recipeSearchButton)
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView)

        recipeRecyclerView.layoutManager = LinearLayoutManager(context)

        val uploadButton = view.findViewById<Button>(R.id.uploadButton)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")  // API 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        recipeSearchButton.setOnClickListener {
            predictedEmotion?.let { emotion ->
                fetchRecipesByEmotion(emotion, 1)
            }
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

        // 이미지 크기 조정 및 업로드
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true)
        imageView.setImageBitmap(resizedBitmap)

        uploadImage(resizedBitmap) { response ->
            response?.let {
                predictedEmotion = it.predictedEmotion
                resultTextView.text = "Predicted Emotion: $predictedEmotion"

                val message = when (predictedEmotion) {
                    "Happy", "Surprise" -> "배가 부르시군요! 디저트를 추천해드릴게요!"
                    else -> "배가 고프시군요? 메인 메뉴를 추천해드릴게요!"
                }

                emotionMessageTextView.text = message
                recipeSearchButton.visibility = View.VISIBLE
            } ?: run {
                resultTextView.text = "Failed to get prediction."
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap, callback: (ApiResponse<String>?) -> Unit) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")  // API 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val filePart = MultipartBody.Part.createFormData("file", "selected_image.jpg", requestBody)
        apiService.predictEmotion(filePart).enqueue(object : Callback<ApiResponse<String>> {
            override fun onResponse(call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("Predict", "Response failed: ${response.code()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                Log.e("Predict", "Error occurred: ${t.message}")
                callback(null)
            }
        })
    }

    private fun fetchRecipesByEmotion(emotion: String, page: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")  // API 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        apiService.getRecipeByEmotion(emotion, page)
            .enqueue(object : Callback<ApiResponse<RecipeCategoryFilteringPreviewDTOList>> {
                override fun onResponse(
                    call: Call<ApiResponse<RecipeCategoryFilteringPreviewDTOList>>,
                    response: Response<ApiResponse<RecipeCategoryFilteringPreviewDTOList>>
                ) {
                    if (response.isSuccessful) {
                        val recipes = response.body()?.result?.recipePreviewDTOList ?: emptyList()

                        // 어댑터 생성 및 설정
                        val adapter = RecipeAdapter(recipes)
                        recipeRecyclerView.adapter = adapter

                        // 클릭 이벤트 처리
                        adapter.onItemClick = { recipeId ->
                            fetchRecipeDetail(recipeId)
                        }
                    } else {
                        Toast.makeText(context, "레시피를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("Recipes", "Failed to fetch recipes: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<RecipeCategoryFilteringPreviewDTOList>>, t: Throwable) {
                    Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Recipes", "Error occurred: ${t.message}", t)
                }
            })
    }


    private fun fetchRecipeDetail(recipeId: Long) {
        apiService.getRecipeDetail(recipeId).enqueue(object : Callback<ApiResponse<RecipeDetailDTO>> {
            override fun onResponse(
                call: Call<ApiResponse<RecipeDetailDTO>>,
                response: Response<ApiResponse<RecipeDetailDTO>>
            ) {
                if (response.isSuccessful) {
                    val recipeDetail = response.body()?.result
                    recipeDetail?.let {
                        // 레시피 상세 화면으로 이동
                        val intent = Intent(context, RecipeDetailActivity::class.java)
                        intent.putExtra("recipeDetail", it)
                        startActivity(intent)
                    } ?: run {
                        Toast.makeText(context, "No recipe details available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load recipe detail.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<RecipeDetailDTO>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

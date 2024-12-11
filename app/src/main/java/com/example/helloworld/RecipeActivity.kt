package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.adapters.RecipeAdapter
import com.example.helloworld.network.ApiResponse
import com.example.helloworld.network.ApiService
import com.example.helloworld.network.RecipeDetailDTO
import com.example.helloworld.network.RecipePreviewDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // API의 기본 URL로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // 인텐트로 받은 레시피 리스트 처리
        val recipes = intent.getSerializableExtra("recipes") as List<RecipePreviewDTO>
        val recyclerView: RecyclerView = findViewById(R.id.recipeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 설정
        val recipeAdapter = RecipeAdapter(recipes)
        recyclerView.adapter = recipeAdapter

        // 아이템 클릭 리스너 설정
        recipeAdapter.onItemClick = { recipeId ->
            // 클릭한 레시피의 ID로 상세 조회
            fetchRecipeDetail(recipeId)
        }
    }

    // 레시피 상세 조회
    private fun fetchRecipeDetail(recipeId: Long) {
        Log.d("RECIPE","recipes: ${recipeId}")

        apiService.getRecipeDetail(recipeId).enqueue(object : Callback<ApiResponse<RecipeDetailDTO>> {
            override fun onResponse(
                call: Call<ApiResponse<RecipeDetailDTO>>,
                response: Response<ApiResponse<RecipeDetailDTO>>
            ) {
                if (response.isSuccessful) {
                    val recipeDetail = response.body()?.result
                    recipeDetail?.let {
                        // 레시피 상세 화면으로 이동
                        val intent = Intent(this@RecipeActivity, RecipeDetailActivity::class.java)
                        intent.putExtra("recipeDetail", it)
                        startActivity(intent)
                    } ?: run {
                        Toast.makeText(this@RecipeActivity, "No recipe details available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RecipeActivity, "Failed to load recipe detail.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<RecipeDetailDTO>>, t: Throwable) {
                Toast.makeText(this@RecipeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

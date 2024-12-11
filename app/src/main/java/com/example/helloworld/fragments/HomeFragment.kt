package com.example.helloworld.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.RecipeDetailActivity
import com.example.helloworld.adapters.RecipeAdapter
import com.example.helloworld.network.ApiResponse
import com.example.helloworld.network.ApiService
import com.example.helloworld.network.RecipeDetailDTO
import com.example.helloworld.network.RecipeListResponse
import com.example.helloworld.network.RecipeSearchPreviewDTOList
import com.example.helloworld.network.getApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)

        // RecyclerView 설정
        recipeRecyclerView.layoutManager = LinearLayoutManager(context)

        // API 서비스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")  // 서버 URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // 초기 레시피 목록 로드
        loadRecipes(1)

        // 검색 버튼 클릭 이벤트
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotBlank()) {
                searchRecipes(query, 1) // 검색 API 호출
            } else {
                Toast.makeText(context, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 전체 레시피 로드
    private fun loadRecipes(page: Int) {
        apiService.getRecipeList(page).enqueue(object : Callback<ApiResponse<RecipeListResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<RecipeListResponse>>,
                response: Response<ApiResponse<RecipeListResponse>>
            ) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.result?.recipePreviewDTOList ?: emptyList()
                    recipeRecyclerView.adapter = RecipeAdapter(recipes).apply {
                        onItemClick = { recipeId ->
                            fetchRecipeDetail(recipeId)  // 클릭 시 레시피 ID 전달
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to load recipes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<RecipeListResponse>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 검색 결과 로드
    private fun searchRecipes(query: String, page: Int) {
        Log.d("SEARCH", "검색어: $query, 페이지: $page")  // 검색 시작 시 로그 출력

        apiService.searchRecipes(query, page).enqueue(object : Callback<ApiResponse<RecipeSearchPreviewDTOList>> {
            override fun onResponse(
                call: Call<ApiResponse<RecipeSearchPreviewDTOList>>,
                response: Response<ApiResponse<RecipeSearchPreviewDTOList>>
            ) {
                if (response.isSuccessful) {
                    // 응답 성공 로그 출력
                    Log.d("SEARCH", "응답 본문: ${response.body()}")
                    Log.d("SEARCH", "검색 결과 성공: ${response.body()?.result?.recipePreviewDTOList?.size ?: 0}개의 레시피 찾음")
                    val recipes = response.body()?.result?.recipePreviewDTOList ?: emptyList()
                    recipeRecyclerView.adapter = RecipeAdapter(recipes).apply {
                        onItemClick = { recipeId ->
                            fetchRecipeDetail(recipeId)  // 클릭 시 레시피 ID 전달
                        }
                    }
                } else {
                    // 응답 실패 로그 출력
                    Log.e("SEARCH", "검색 결과 실패: ${response.message()}")
                    Toast.makeText(context, "검색 결과를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<RecipeSearchPreviewDTOList>>, t: Throwable) {
                // 네트워크 실패 로그 출력
                Log.e("SEARCH", "검색 요청 실패: ${t.message}", t)
                Toast.makeText(context, "오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 레시피 상세 정보 로드
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


package com.example.helloworld.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.RecipeActivity
import com.example.helloworld.RecipeDetailActivity
import com.example.helloworld.adapters.IngredientsAdapter
import com.example.helloworld.adapters.RecipeAdapter
import com.example.helloworld.network.ApiResponse
import com.example.helloworld.network.ApiService
import com.example.helloworld.network.IngredientsPreviewListDTO
import com.example.helloworld.network.RecipeContainingIngredientsResultDTOList
import com.example.helloworld.network.RecipeDetailDTO
import com.example.helloworld.network.RecipePreviewDTO
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IngredientsFragment : Fragment() {
    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var pageTextView: TextView
    private lateinit var adapter: IngredientsAdapter
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private var currentPage = 1
    private val selectedIngredients = mutableListOf<String>()  // 선택된 재료 이름 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // IngredientsRecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        pageTextView = view.findViewById(R.id.pageTextView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = IngredientsAdapter(
            onDeleteClick = { ingredientId -> onDeleteIngredient(ingredientId) },
            onIngredientCheck = { ingredientName -> toggleIngredientSelection(ingredientName) }
        )
        recyclerView.adapter = adapter

        // RecipeRecyclerView 초기화
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView) // recipeRecyclerView 초기화
        recipeRecyclerView.layoutManager = LinearLayoutManager(context)
        recipeAdapter = RecipeAdapter(emptyList()) // 초기 빈 리스트로 어댑터 설정
        recipeRecyclerView.adapter = recipeAdapter

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")  // 서버 주소에 맞춰 수정
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // 첫 페이지 로드
        loadIngredientsPage(currentPage)

        // 페이지 변경 버튼 설정
        view.findViewById<View>(R.id.nextPageButton).setOnClickListener {
            currentPage++
            loadIngredientsPage(currentPage)
        }
        view.findViewById<View>(R.id.prevPageButton).setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                loadIngredientsPage(currentPage)
            }
        }

        // 레시피 조회 버튼 설정
        view.findViewById<View>(R.id.recipeLookupButton).setOnClickListener {
            // 선택된 재료들로 레시피 조회
            fetchRecipesForSelectedIngredients()
        }
    }

    private fun loadIngredientsPage(page: Int) {
        progressBar.visibility = View.VISIBLE
        apiService.getIngredientsList(page).enqueue(object : Callback<ApiResponse<IngredientsPreviewListDTO>> {
            override fun onResponse(
                call: Call<ApiResponse<IngredientsPreviewListDTO>>,
                response: Response<ApiResponse<IngredientsPreviewListDTO>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    result?.let {
                        adapter.setIngredients(it.ingredientsPreviewDTOList)
                        pageTextView.text = "Page ${page}/${it.totalPage}"
                    } ?: run {
                        Toast.makeText(context, "No data available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load ingredients.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<IngredientsPreviewListDTO>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onDeleteIngredient(ingredientId: Long) {
        apiService.deleteIngredient(ingredientId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Ingredient deleted successfully.", Toast.LENGTH_SHORT).show()
                    loadIngredientsPage(currentPage)  // 갱신된 목록을 다시 로드
                } else {
                    Toast.makeText(context, "Failed to delete ingredient.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleIngredientSelection(ingredientName: String) {
        if (selectedIngredients.contains(ingredientName)) {
            selectedIngredients.remove(ingredientName)  // 선택 해제 시 리스트에서 제거
        } else {
            selectedIngredients.add(ingredientName)  // 선택 시 리스트에 추가
        }
    }

    private fun fetchRecipesForSelectedIngredients() {
        view?.findViewById<View>(R.id.recipeLookupButton)?.setOnClickListener {
            if (selectedIngredients.isEmpty()) {
                Toast.makeText(context, "Please select ingredients first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            apiService.getRecipesContainingIngredients(currentPage, selectedIngredients)
                .enqueue(object : Callback<ApiResponse<RecipeContainingIngredientsResultDTOList>> {
                    override fun onResponse(
                        call: Call<ApiResponse<RecipeContainingIngredientsResultDTOList>>,
                        response: Response<ApiResponse<RecipeContainingIngredientsResultDTOList>>
                    ) {
                        progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            val recipes = response.body()?.result?.recipePreviewDTOList
                            if (recipes.isNullOrEmpty()) {
                                Toast.makeText(context, "No recipes found.", Toast.LENGTH_SHORT).show()
                            } else {

                                val intent = Intent(context, RecipeActivity::class.java)
                                intent.putExtra("recipes", ArrayList(recipes)) // 레시피 리스트 전달
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(context, "Failed to fetch recipes.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponse<RecipeContainingIngredientsResultDTOList>>,
                        t: Throwable
                    ) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}




package com.example.helloworld.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit 인터페이스 정의
interface ApiService {

    @Multipart
    @POST("predict/")
    fun predictEmotion(
        @Part file: MultipartBody.Part
    ): Call<ApiResponse<String>>  // API 응답을 ApiResponse로 받음

    @Multipart
    @POST("detect/") // API 엔드포인트
    fun detectIngredients(@Part file: MultipartBody.Part): Call<ApiResponse<List<DetectedClass>>>

    @GET("/api/recipes/ingredients/list")
    fun getIngredientsList(
        @Query("page") page: Int
    ): Call<ApiResponse<IngredientsPreviewListDTO>>

    @GET("/api/recipes/list")
    fun getRecipeList(
        @Query("page") page: Int
    ): Call<ApiResponse<RecipeListResponse>>

    @DELETE("/api/recipes/delete/ingredients/{ingredientsId}")
    fun deleteIngredient(
        @Path("ingredientsId") ingredientsId: Long
    ): Call<Void>

    @GET("/api/members/my/profile/{memberId}")
    fun getMyProfile(
        @Path("memberId") memberId: Long
    ): Call<ApiResponse<MyProfileResponse>>

    @GET("/api/recipes/search/list")
    fun searchRecipes(
        @Query("search") query: String,  // 검색어
        @Query("page") page: Int         // 페이지 번호
    ): Call<ApiResponse<RecipeSearchPreviewDTOList>>

    @GET("/api/recipes/emotion/filtering/list")
    fun getRecipeByEmotion(
        @Query("emotion") emotion: String,  // 감정 (Angry, Happy, Sad 등)
        @Query("page") page: Int           // 페이지 번호
    ): Call<ApiResponse<RecipeCategoryFilteringPreviewDTOList>>  // 응답은 RecipeListResponse 형태

    @GET("/api/recipes/containing/ingredients/list")
    fun getRecipesContainingIngredients(
        @Query("page") page: Int,
        @Query("ingredientNames") ingredientNames: List<String>
    ): Call<ApiResponse<RecipeContainingIngredientsResultDTOList>>

    @GET("/api/recipes/{recipeId}")
    fun getRecipeDetail(
        @Path("recipeId") recipeId: Long
    ): Call<ApiResponse<RecipeDetailDTO>>
}

// Retrofit 객체를 생성하는 함수
fun getApiService(): ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8000/")  // FastAPI 서버 URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ApiService::class.java)
}

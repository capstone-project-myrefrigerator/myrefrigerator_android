package com.example.helloworld.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetectedEmotion(
    val label: String,  // 감정 라벨 (예: "happy", "sad")
    val confidence: Float  // 확신도 (0.0 - 1.0)
)

data class DetectedClass(
    val name: String,      // 재료 이름
    val url: String        // 해당 재료의 URL
)

data class IngredientsPreviewDTO(
    val ingredientsId: Long,  // 재료 ID
    val name: String,         // 재료 이름
    val url: String           // 재료 이미지 URL
)

data class IngredientsPreviewListDTO(
    @SerializedName("ingredientsPreviewDTOList")
    val ingredientsPreviewDTOList: List<IngredientsPreviewDTO>,   // 재료 목록
    val listSize: Int,         // 페이지당 항목 수
    val totalPage: Int,        // 전체 페이지 수
    val totalElements: Long,   // 전체 항목 수
    val isFirst: Boolean,      // 첫 번째 페이지 여부
    val isLast: Boolean        // 마지막 페이지 여부
)

data class MyProfileResponse(
    val memberId: Long,
    val name: String,
    val weight: Int,
    val height: Int
)

data class RecipeListResponse(
    @SerializedName("recipePreviewDTOList")
    val recipePreviewDTOList: List<RecipePreviewDTO>,   // 레시피 미리보기 리스트
    val listSize: Int,         // 페이지당 항목 수
    val totalPage: Int,        // 전체 페이지 수
    val totalElements: Long,   // 전체 항목 수
    val isFirst: Boolean,      // 첫 번째 페이지 여부
    val isLast: Boolean        // 마지막 페이지 여부
)

data class RecipeSearchPreviewDTOList(
    @SerializedName("recipeSearchPreviewDTOList")
    val recipePreviewDTOList: List<RecipePreviewDTO>,   // 레시피 미리보기 리스트
    val listSize: Int,         // 페이지당 항목 수
    val totalPage: Int,        // 전체 페이지 수
    val totalElements: Long,   // 전체 항목 수
    val isFirst: Boolean,      // 첫 번째 페이지 여부
    val isLast: Boolean        // 마지막 페이지 여부
)

data class RecipeCategoryFilteringPreviewDTOList(
    @SerializedName("recipeCategoryFilteringPreviewDTOList")
    val recipePreviewDTOList: List<RecipePreviewDTO>,   // 레시피 미리보기 리스트
    val listSize: Int,         // 페이지당 항목 수
    val totalPage: Int,        // 전체 페이지 수
    val totalElements: Long,   // 전체 항목 수
    val isFirst: Boolean,      // 첫 번째 페이지 여부
    val isLast: Boolean        // 마지막 페이지 여부
)

data class RecipeContainingIngredientsResultDTOList(
    @SerializedName("recipeContainingIngredientsResultDTOList")
    val recipePreviewDTOList: List<RecipePreviewDTO>,   // 레시피 미리보기 리스트
    val listSize: Int,         // 페이지당 항목 수
    val totalPage: Int,        // 전체 페이지 수
    val totalElements: Long,   // 전체 항목 수
    val isFirst: Boolean,      // 첫 번째 페이지 여부
    val isLast: Boolean        // 마지막 페이지 여부
)

data class RecipePreviewDTO(
    val recipeId: Long,
    val name: String,
    val contents: String,
    val protein: Int,
    val calorie: Int,
    val fat: Int,
    val carbohydrate: Int,
    val category: String,
    val recipImgResponseDTOList: List<RecipeImgDTO>, // 레시피 이미지 목록
    val ingredientsList: List<String>   // 재료 목록
): Serializable

data class RecipeImgDTO(
    val recipeImgUrl: String,   // 레시피 이미지 URL
    val isThumbnail: Boolean    // 썸네일 여부
): Serializable

data class RecipeDetailDTO(
    val recipeId: Long,
    val name: String,
    val contents: String,
    val protein: Int,
    val calorie: Int,
    val fat: Int,
    val carbohydrate: Int,
    val category: String,
    val recipImgResponseDTOList: List<RecipeImgDTO>,
    val ingredientsList: List<String>
): Serializable

data class ApiResponse<T>(
    @SerializedName("predicted_emotion") val predictedEmotion: String?,  // 감정 목록
    @SerializedName("all_detected_classes") val allDetectedClasses: List<DetectedClass>?,   // 탐지된 모든 재료
    @SerializedName("newly_inserted_classes")val newlyInsertedClasses: List<DetectedClass>?, // 새로 삽입된 재료
    @SerializedName("already_existing_classes") val alreadyExistingClasses: List<DetectedClass>?, // 이미 존재하는 재료
    @SerializedName("message") val message: String?,   // 처리 메시지
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("result") val result: T?  // `result`를 전체 응답으로 사용
): Serializable


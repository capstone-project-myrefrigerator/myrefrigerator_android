package com.example.helloworld

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.helloworld.network.RecipeDetailDTO
import com.example.helloworld.network.RecipePreviewDTO

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImageView: ImageView
    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeContentsTextView: TextView
    private lateinit var recipeNutritionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        recipeImageView = findViewById(R.id.recipeDetailImageView)
        recipeNameTextView = findViewById(R.id.recipeDetailNameTextView)
        recipeContentsTextView = findViewById(R.id.recipeDetailContentsTextView)
        recipeNutritionTextView = findViewById(R.id.recipeDetailNutritionTextView)

        val recipeDetail = intent.getSerializableExtra("recipeDetail") as RecipeDetailDTO
        recipeNameTextView.text = recipeDetail.name
        recipeContentsTextView.text = recipeDetail.contents
        recipeNutritionTextView.text = "탄수화물: ${recipeDetail.carbohydrate}g, 단백질: ${recipeDetail.protein}g, 지방: ${recipeDetail.fat}g, 칼로리: ${recipeDetail.calorie}kcal"

        // 레시피 이미지 설정
        val imageUrl = recipeDetail.recipImgResponseDTOList.firstOrNull()?.recipeImgUrl
        Glide.with(this)
            .load(imageUrl ?: R.drawable.ic_error)
            .placeholder(R.drawable.ic_placeholder)
            .into(recipeImageView)
    }
}

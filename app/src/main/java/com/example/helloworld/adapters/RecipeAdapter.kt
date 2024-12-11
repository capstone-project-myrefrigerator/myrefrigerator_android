package com.example.helloworld.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helloworld.R
import com.example.helloworld.network.RecipePreviewDTO


class RecipeAdapter(private val recipeList: List<RecipePreviewDTO>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    var onItemClick: ((Long) -> Unit)? = null  // 아이템 클릭 리스너 추가

    fun setOnRecipeClickListener(listener: (Long) -> Unit) {
        onItemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe)

        // 아이템 클릭 시 onItemClick 호출
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(recipe.recipeId)  // 레시피 ID를 전달
        }
    }

    override fun getItemCount(): Int = recipeList.size

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val recipeContentsTextView: TextView = itemView.findViewById(R.id.recipeContentsTextView)
        private val recipeNutritionTextView: TextView = itemView.findViewById(R.id.recipeNutritionTextView)

        fun bind(recipe: RecipePreviewDTO) {
            // 텍스트 설정
            recipeNameTextView.text = recipe.name ?: "이름 없음"

            // contents 텍스트를 100자로 제한
            recipeContentsTextView.text = if (recipe.contents != null && recipe.contents.length > 100) {
                "${recipe.contents.substring(0, 100)}..."
            } else {
                recipe.contents ?: "내용 없음"
            }

            // 영양 성분을 한 줄로 출력
            recipeNutritionTextView.text = "탄수화물: ${recipe.carbohydrate ?: 0}g, 단백질: ${recipe.protein ?: 0}g\n, 지방: ${recipe.fat ?: 0}g, 칼로리: ${recipe.calorie ?: 0}kcal"

            // 이미지 설정
            val imageUrl = recipe.recipImgResponseDTOList.firstOrNull()?.recipeImgUrl
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder) // 로딩 중 기본 이미지
                    .error(R.drawable.ic_error)             // 오류 시 기본 이미지
                    .into(recipeImageView)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_error) // 기본 오류 이미지
                    .into(recipeImageView)
            }
        }
    }
}

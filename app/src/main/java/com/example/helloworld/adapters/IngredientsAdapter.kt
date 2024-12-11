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
import com.example.helloworld.network.IngredientsPreviewDTO

class IngredientsAdapter(private val onDeleteClick: (Long) -> Unit, private val onIngredientCheck: (String) -> Unit) : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {
    private val ingredients = mutableListOf<IngredientsPreviewDTO>()

    fun setIngredients(newIngredients: List<IngredientsPreviewDTO>) {
        ingredients.clear()
        ingredients.addAll(newIngredients)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view, onDeleteClick, onIngredientCheck)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredients.size

    class IngredientViewHolder(itemView: View, private val onDeleteClick: (Long) -> Unit, private val onIngredientCheck: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ingredientImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.ingredientNameTextView)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        private val checkBox: android.widget.CheckBox = itemView.findViewById(R.id.ingredientCheckBox)

        fun bind(ingredient: IngredientsPreviewDTO) {
            nameTextView.text = ingredient.name

            // drawable 폴더에서 이미지 로드
            val context = itemView.context
            val imageName = ingredient.name.toLowerCase(java.util.Locale.ROOT)
            val imageResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

            if (imageResId != 0) {
                // 이미지가 존재하면 Glide로 로드
                Glide.with(context)
                    .load(imageResId)
                    .placeholder(R.drawable.ic_placeholder)  // 로딩 중 기본 이미지
                    .error(R.drawable.ic_error)  // 오류 발생 시 기본 이미지
                    .into(imageView)
            } else {
                // 이미지가 존재하지 않으면 오류 이미지 로드
                Log.e("IngredientViewHolder", "Image not found for: $imageName")
                Glide.with(context)
                    .load(R.drawable.ic_error)  // 기본 오류 이미지
                    .into(imageView)
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onIngredientCheck(ingredient.name)  // 선택된 재료 이름을 리스트에 추가
                } else {
                    onIngredientCheck(ingredient.name)  // 선택 해제 시 리스트에서 제거
                }
            }

            deleteButton.setOnClickListener {
                onDeleteClick(ingredient.ingredientsId)
            }
        }
    }
}

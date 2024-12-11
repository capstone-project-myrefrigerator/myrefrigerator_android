import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helloworld.R
import com.example.helloworld.network.IngredientsPreviewDTO

class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.ingredientImageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.ingredientNameTextView)

    fun bind(ingredient: IngredientsPreviewDTO) {
        nameTextView.text = ingredient.name

        // drawable 폴더에서 이미지 로드
        val context = itemView.context

        // 이미지 이름을 소문자로 변환 (drawable 폴더에 저장된 이름에 맞추기)
        val imageName = ingredient.name.toLowerCase()

        // 이미지 리소스를 가져오는 방법
        val imageResId = context.resources.getIdentifier(imageName + ".jpg", "drawable", context.packageName)

        // 이미지가 존재하는지 확인
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
    }
}

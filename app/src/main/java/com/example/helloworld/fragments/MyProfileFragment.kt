package com.example.helloworld.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.helloworld.R
import com.example.helloworld.network.ApiService
import com.example.helloworld.network.ApiResponse
import com.example.helloworld.network.MyProfileResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyProfileFragment : Fragment() {
    private lateinit var apiService: ApiService
    private lateinit var nameTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var profileImageView: ImageView  // ImageView 추가

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 요소 초기화
        nameTextView = view.findViewById(R.id.nameTextView)
        weightTextView = view.findViewById(R.id.weightTextView)
        heightTextView = view.findViewById(R.id.heightTextView)
        profileImageView = view.findViewById(R.id.profileImageView)  // ImageView 초기화

        // 프로필 이미지 설정 (drawable 폴더에서 static 이미지 사용)
        profileImageView.setImageResource(R.drawable.testqwak01)

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")  // 서버 URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // 프로필 데이터 로드
        loadProfile(1)  // memberId 1로 고정
    }

    private fun loadProfile(memberId: Long) {
        apiService.getMyProfile(memberId).enqueue(object : Callback<ApiResponse<MyProfileResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<MyProfileResponse>>,
                response: Response<ApiResponse<MyProfileResponse>>
            ) {
                if (response.isSuccessful) {
                    val profile = response.body()?.result
                    if (profile != null) {
                        // 프로필 데이터를 UI에 설정
                        nameTextView.text = "Name: ${profile.name}"
                        weightTextView.text = "Weight: ${profile.weight} kg"
                        heightTextView.text = "Height: ${profile.height} cm"
                    } else {
                        Toast.makeText(context, "Profile data is empty", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<MyProfileResponse>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}



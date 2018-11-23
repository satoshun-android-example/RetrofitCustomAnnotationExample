package com.github.satoshun.example.sample

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.github.satoshun.example.sample.databinding.MainActBinding
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers

class MainActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<MainActBinding>(this, R.layout.main_act)

    if (savedInstanceState == null) {
      supportFragmentManager.commit {
        add(R.id.fragment, MainFragment())
      }
    }

    val client = OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor())
      .addInterceptor(Auth2Interceptor())
      .build()
    val service: ApiService = Retrofit
      .Builder()
      .client(client)
      .baseUrl("https://hoge.com")
      .build()
      .create()

    val callback = object : Callback<Unit> {
      override fun onFailure(call: Call<Unit>, t: Throwable) {
        println(t)
      }

      override fun onResponse(call: Call<Unit>, response: retrofit2.Response<Unit>) {
        println(call)
      }
    }

    service.useAnnotationLogin().enqueue(callback)
    service.noauthlogin().enqueue(callback)
    service.useHeaderLogin().enqueue(callback)
  }
}

class AuthInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()

    val invocation = request.tag(Invocation::class.java)
    val authAnnotation = invocation?.method()?.getAnnotation(RequireAuth::class.java)
    if (authAnnotation != null) {
      request = request
        .newBuilder()
        .addHeader("Authorization", "Basic AAAAA").build()
    }
    return chain.proceed(request)
  }
}

class Auth2Interceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()

    if (request.header("Auth") != null) {
      request = request
        .newBuilder()
        .addHeader("Authorization", "Basic BBBBB").build()
    }
    return chain.proceed(request)
  }
}

annotation class RequireAuth

interface ApiService {
  @RequireAuth
  @GET("login")
  fun useAnnotationLogin(): retrofit2.Call<Unit>

  @GET("noauthlogin")
  fun noauthlogin(): retrofit2.Call<Unit>

  @Headers("Auth: true")
  @GET("useheaderlogin")
  fun useHeaderLogin(): retrofit2.Call<Unit>
}

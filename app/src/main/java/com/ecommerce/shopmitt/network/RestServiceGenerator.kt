package com.ecommerce.shopmitt.network


import com.ecommerce.shopmitt.BuildConfig
import com.ecommerce.shopmitt.network.interceptors.UserAgentInterceptor
import com.ecommerce.shopmitt.utils.AlisApplication
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class RestServiceGenerator {

    private var apiService: RestApiClient? = null

    init {

        val cacheDir = File(AlisApplication.instance.applicationContext.cacheDir, "AlisCache")

        val DISK_CACHE_SIZE = 50 * 1024 * 1024 // 50MB
        val cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .cache(cache)
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .addInterceptor(UserAgentInterceptor())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        apiService = retrofit.create(RestApiClient::class.java)
    }

    fun getService(): RestApiClient? {
        return apiService
    }


    /*  private object HOLDER {
          val INSTANCE = RestServiceGenerator()
      }

      companion object {
          //val instance: LogHelper = HOLDER.INSTANCE
          val instance: RestServiceGenerator by lazy { HOLDER.INSTANCE }
      }*/

}
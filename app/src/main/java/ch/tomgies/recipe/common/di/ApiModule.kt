package ch.tomgies.recipe.common.di

import android.content.Context
import ch.tomgies.recipe.R
import ch.tomgies.recipe.data.api.RecipesApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient {
        val networkTimeoutInSec = 20L
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(networkTimeoutInSec, TimeUnit.SECONDS)
            .readTimeout(networkTimeoutInSec, TimeUnit.SECONDS)
            .writeTimeout(networkTimeoutInSec, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(context: Context, moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.base_url_recipe))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Provides
    fun provideRecipeApi(retrofit: Retrofit): RecipesApi = retrofit.create(RecipesApi::class.java)
}

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
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

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
        val networkTimeout = 20.seconds
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(networkTimeout)
            .connectTimeout(networkTimeout)
            .readTimeout(networkTimeout)
            .writeTimeout(networkTimeout)
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

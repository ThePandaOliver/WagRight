package dev.pandasystems.wagright.di

import dev.pandasystems.wagright.data.remote.DogApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideJson(): Json = Json {
		ignoreUnknownKeys = true
	}

	@Provides
	@Singleton
	fun provideRetrofit(json: Json): Retrofit {
		return Retrofit.Builder()
			.baseUrl(DogApiService.BASE_URL)
			.addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
			.build()
	}

	@Provides
	@Singleton
	fun provideDogApiService(retrofit: Retrofit): DogApiService {
		return retrofit.create(DogApiService::class.java)
	}
}

package dev.pandasystems.wagright.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pandasystems.wagright.data.local.AppDatabase
import dev.pandasystems.wagright.data.local.DogDao
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Provides
	@Singleton
	fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
		return Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"wagright_database"
		).build()
	}

	@Provides
	@Singleton
	fun provideDogDao(database: AppDatabase): DogDao {
		return database.dogDao()
	}
}

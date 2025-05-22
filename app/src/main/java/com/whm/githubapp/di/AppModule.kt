package com.whm.githubapp.di

import android.content.Context
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.network.GitHubRepoService
import com.whm.githubapp.network.GitHubUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserSessionManager(@ApplicationContext context: Context): UserSessionManager {
        return UserSessionManager(context)
    }

    @Provides
    @Singleton
    fun provideGitHubRepoService(): GitHubRepoService {
        return GitHubRepoService.create()
    }

    @Provides
    @Singleton
    fun provideGitHubUserService(): GitHubUserService {
        return GitHubUserService.create()
    }
}

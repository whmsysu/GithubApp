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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.Response
import com.whm.githubapp.datastore.dataStore
import com.whm.githubapp.datastore.TokenProvider
import com.whm.githubapp.repository.RepoRepository
import com.whm.githubapp.repository.UserRepository
import okhttp3.Cache
import java.io.File
import com.whm.githubapp.network.ETagStore
import kotlinx.coroutines.runBlocking
import com.whm.githubapp.network.NetworkConstants

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
    fun provideAuthorizationInterceptor(@ApplicationContext context: Context, tokenProvider: TokenProvider): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val token = tokenProvider.token.value
            if (token.isNullOrEmpty()) {
                val response = chain.proceed(original)
                if (response.code == 401 || response.code == 403) {
                    runBlocking { UserSessionManager(context).clearToken() }
                }
                return@Interceptor response
            }
            val authed = original.newBuilder()
                .addHeader("Authorization", "token $token")
                .build()
            val response = chain.proceed(authed)
            if (response.code == 401 || response.code == 403) {
                runBlocking { UserSessionManager(context).clearToken() }
            }
            response
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context, authInterceptor: Interceptor, eTagStore: ETagStore): OkHttpClient {
        val logging: Interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val cacheSize = 10L * 1024L * 1024L
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        val cacheInterceptor = Interceptor { chain ->
            val request = chain.request()
            val isGet = request.method.equals("GET", ignoreCase = true)
            val isSearchOrTrending = request.url.encodedPath.startsWith("/search/repositories")
            val etag = if (isGet && isSearchOrTrending) eTagStore.get(request.url.toString()) else null
            val reqWithEtag = if (etag != null) request.newBuilder().header("If-None-Match", etag).build() else request
            val response = chain.proceed(reqWithEtag)
            if (isGet && isSearchOrTrending) {
                val newEtag = response.header("ETag")
                if (!newEtag.isNullOrEmpty()) {
                    eTagStore.save(request.url.toString(), newEtag)
                }
                val fromCache = (response.code == 304) || (response.cacheResponse != null)
                return@Interceptor response.newBuilder()
                    .header("X-From-Cache", fromCache.toString())
                    .header("Cache-Control", "public, max-age=${NetworkConstants.CACHE_MAX_AGE_SECONDS}")
                    .build()
            }
            response
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(logging)
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubRepoService(retrofit: Retrofit): GitHubRepoService {
        return retrofit.create(GitHubRepoService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubUserService(retrofit: Retrofit): GitHubUserService {
        return retrofit.create(GitHubUserService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepoRepository(service: GitHubRepoService): RepoRepository {
        return RepoRepository(service)
    }

    @Provides
    @Singleton
    fun provideUserRepository(service: GitHubUserService): UserRepository {
        return UserRepository(service)
    }
}

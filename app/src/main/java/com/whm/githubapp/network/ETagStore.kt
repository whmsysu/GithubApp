package com.whm.githubapp.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ETagStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("etag_store", Context.MODE_PRIVATE)

    fun get(url: String): String? = prefs.getString(url, null)

    fun save(url: String, etag: String) {
        prefs.edit().putString(url, etag).apply()
    }
}



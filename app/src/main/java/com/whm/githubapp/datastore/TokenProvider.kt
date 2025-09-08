package com.whm.githubapp.datastore

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProvider @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val token: StateFlow<String?> = context.dataStore.data
        .map { it[UserSessionManager.TOKEN_KEY] }
        .stateIn(appScope, SharingStarted.Eagerly, null)
}



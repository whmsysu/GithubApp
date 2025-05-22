package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubUser
import com.whm.githubapp.network.GitHubUserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userSession: UserSessionManager,
    private val service: GitHubUserService
) : ViewModel() {

    val token = userSession.token.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _user = MutableStateFlow<GitHubUser?>(null)
    val user: StateFlow<GitHubUser?> = _user

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    init {
        _loading.value = true
        viewModelScope.launch {
            token.collect { tokenValue ->
                if (!tokenValue.isNullOrEmpty()) {
                    _loading.value = true
                    try {
                        val userInfo = service.getUserInfo("token $tokenValue")
                        _user.value = userInfo
                    } catch (_: Exception) {
                    }
                }
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userSession.clearToken()
            _user.value = null
        }
    }
}
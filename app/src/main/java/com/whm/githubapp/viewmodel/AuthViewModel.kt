package com.whm.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whm.githubapp.datastore.UserSessionManager
import com.whm.githubapp.model.GitHubUser
import com.whm.githubapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userSession: UserSessionManager,
    private val userRepository: UserRepository
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
                            val userInfo = userRepository.getUserInfo()
                            _user.value = userInfo
                        } catch (_: Exception) {
                        } finally {
                            _loading.value = false
                        }
                    } else {
                        _user.value = null
                        _loading.value = false
                    }
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
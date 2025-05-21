package com.whm.githubapp.viewmodel

        import android.content.Context
        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.viewModelScope
        import com.whm.githubapp.datastore.UserSessionManager
        import com.whm.githubapp.model.GitHubUser
        import com.whm.githubapp.network.GitHubUserService
        import kotlinx.coroutines.flow.MutableStateFlow
        import kotlinx.coroutines.flow.SharingStarted
        import kotlinx.coroutines.flow.StateFlow
        import kotlinx.coroutines.flow.stateIn
        import kotlinx.coroutines.launch

        class AuthViewModel(context: Context) : ViewModel() {
            private val userSession = UserSessionManager(context)
            val token = userSession.token.stateIn(viewModelScope, SharingStarted.Lazily, null)

            private val _user = MutableStateFlow<GitHubUser?>(null)
            val user: StateFlow<GitHubUser?> = _user

            init {
                viewModelScope.launch {
                    token.collect { tokenValue ->
                        if (!tokenValue.isNullOrEmpty()) {
                            try {
                                val userInfo = GitHubUserService.create().getUserInfo("token $tokenValue")
                                _user.value = userInfo
                            } catch (_: Exception) {}
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
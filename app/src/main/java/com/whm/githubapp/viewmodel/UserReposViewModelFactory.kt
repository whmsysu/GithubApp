package com.whm.githubapp.viewmodel

        import android.content.Context
        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.ViewModelProvider

        class UserReposViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserReposViewModel(context) as T
            }
        }
package com.example.appwebgps.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appwebgps.utils.StringConstants
import com.example.appwebgps.login.LoginRepository
import com.example.appwebgps.network.utils.result.Result
import com.example.appwebgps.network.utils.result.asSuccess
import com.example.appwebgps.security.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean?> = _isLoggedIn

    private val _isUserRemembered = MutableLiveData(false)
    val isUserRemembered: LiveData<Boolean?> = _isUserRemembered

    init {
        _isUserRemembered.value = preferenceHelper.getBoolean(StringConstants.isUserRemembered)
    }

    fun getSavedLoginAndPassword(): HashMap<String, String?> {
        val response = HashMap<String, String?>()
        response[StringConstants.userName] = preferenceHelper.getString(StringConstants.userName)
        response[StringConstants.userPassword] =
            preferenceHelper.getString(StringConstants.userPassword)
        return response
    }

    fun removeLoginAndPassword() {
        with(preferenceHelper) {
            removeString(StringConstants.userName)
            removeString(StringConstants.userPassword)
            removeBoolean(StringConstants.isUserRemembered)
        }
    }

    fun doLogin(login: String, password: String) {
        val loginDeferred = viewModelScope.async {
            loginRepository.deleteUserInfo()
            when (val loginResponse = loginRepository.getToken(login, password)) {
                is Result.Success -> {
                    val token = loginResponse.asSuccess().value?.accessToken
                    loginRepository.saveToken(token)
                    with(preferenceHelper) {
                        removeString(StringConstants.userName)
                        removeString(StringConstants.userPassword)
                        saveString(StringConstants.userName, login)
                        saveString(StringConstants.userPassword, password)
                        saveBoolean(StringConstants.isUserRemembered, true)
                    }
                }
                is Result.Failure.HttpError -> {
                    _errorMessage.postValue(loginResponse.statusCode.toString())
                    _isLoggedIn.postValue(false)
                }
                is Result.Failure.Error -> {
                    _errorMessage.postValue(loginResponse.error.message)
                }
            }
        }
        viewModelScope.launch {
            loginDeferred.await()
            when (val userIdResponse = loginRepository.getUserId(login)) {
                is Result.Success -> {
                    val userId = userIdResponse.asSuccess().value?.get(0)?.id
                    loginRepository.saveUserId(userId)
                    _isLoggedIn.postValue(true)
                }
                is Result.Failure.HttpError -> {
                    _errorMessage.postValue(userIdResponse.statusCode.toString())
                    _isLoggedIn.postValue(false)
                }
                is Result.Failure.Error -> {
                    _errorMessage.postValue(userIdResponse.error.message)
                }
            }
        }
    }
}
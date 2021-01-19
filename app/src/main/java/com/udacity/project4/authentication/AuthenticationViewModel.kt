package com.udacity.project4.authentication

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.utils.AuthenticationState

class AuthenticationViewModel(app: Application) : BaseViewModel(app)  {

    private val _eventStartAuthentication = MutableLiveData<Boolean>()
    val eventStartAuthentication: LiveData<Boolean>
        get() = _eventStartAuthentication

    fun onStartAuthentication() {
        _eventStartAuthentication.value = true
    }

    fun onStartAuthenticationComplete() {
        _eventStartAuthentication.value = false
    }

}
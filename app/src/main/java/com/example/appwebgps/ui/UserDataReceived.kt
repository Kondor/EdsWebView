package com.example.appwebgps.ui

interface UserDataReceived {
    fun sendUserData(login: String, password: String)
    fun isLoggedIn(state: Boolean)
}
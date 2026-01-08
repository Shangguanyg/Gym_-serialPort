package com.ganainy.balanceboardcontrolapp.network

data class RequestResult<T>(var resultState: ResultState, var data: T)

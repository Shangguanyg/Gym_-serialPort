package com.ganainy.balanceboardcontrolapp.data

data class Command
(
    var id: Int,
    var type: Int,
    var exercise: String,
    var level: Int,
    var signalPeriod: Int,
    var changeTime: Int
)

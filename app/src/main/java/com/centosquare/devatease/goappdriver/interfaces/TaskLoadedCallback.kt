package com.centosquare.devatease.goappdriver.interfaces

interface TaskLoadedCallback {

    fun onTaskDone(vararg values: Any)
    fun onTimeDone(vararg value: String)
}
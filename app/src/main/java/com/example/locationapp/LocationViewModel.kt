package com.example.locationapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {

    private var _location =  mutableStateOf<Location?>(null)
     var location : State<Location?> = _location

    fun updateLoc(newLocation : Location){
        _location.value = newLocation
    }
}
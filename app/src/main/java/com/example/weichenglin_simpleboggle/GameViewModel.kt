package com.example.weichenglin_simpleboggle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    val score: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}
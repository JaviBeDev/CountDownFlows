package com.example.counterflows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countDownFlow = flow {
        val startingValue = 10
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    init {
        collectFlow()
        reduceResult()
        flattenFlows()
    }

    private fun collectFlow() {
        viewModelScope.launch {
            val countEven = countDownFlow
                .filter { time ->
                    time % 2 == 0
                }
                .map { time ->
                    time * time
                }
                .onEach { time ->
                    println(time)
                }
                .count {
                    it % 2 == 0
                }
            println("The count is $countEven")
        }
    }
    
    private fun reduceResult() {
        viewModelScope.launch { 
            val reduceResult = countDownFlow
                .reduce { accumulator, value ->
                    accumulator + value
                }
            println("The acumulated will be: $reduceResult")
        }
    }

    private fun flattenFlows() {
        val flow1 = flow {
            delay(250L)
            emit("Pokemon Bulbasaur")
            delay(1000L)
            emit("Pokemon Charmander")
            delay(100L)
            emit("Pokemon Squirtle")
        }
        viewModelScope.launch {
            flow1.onEach {
                println("Flow: $it Atrapar?!!")
            }
                .conflate()
                .collect {
                    println("Flow: $it esta siendo atrapado")
                    delay(1500L)
                    println("Flow: $it AH SIDO ATRAPADO!!!")
                }
        }
    }
}

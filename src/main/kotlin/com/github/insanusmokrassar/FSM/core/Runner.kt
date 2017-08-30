package com.github.insanusmokrassar.FSM.core

import java.util.*
import java.util.logging.Logger

class Runner(private val states: List<State>, private val firstState: Int = 0) : (String) -> Unit {
    @Throws(IllegalArgumentException::class)
    override fun invoke(input: String) {
        val stack =  Stack<Int>()
        val inputDeque = ArrayDeque<String>(input.toCharArray().map { it.toString() })
        var stateNum = firstState
        while (inputDeque.isNotEmpty()) {
            val currentInput = inputDeque.peek()
            val currentState = states[stateNum]
            if (currentState.regex.matches(currentInput)) {
                try {
                    currentState.action(currentInput)
                } catch (e: Throwable) {
                    Logger.getGlobal().throwing(
                            javaClass.simpleName,
                            "invoke",
                            e
                    )
                }
                if (currentState.stack) {
                    stack.push(stateNum + 1)
                }
                stateNum = if (currentState.next != null) {
                    currentState.next!!
                } else {
                    stack.pop()
                }
                if (currentState.accept) {
                    inputDeque.pop()
                }
            } else {
                if (currentState.error) {
                    throw IllegalArgumentException("Error in read input")
                } else {
                    stateNum++
                }
            }
        }
        if (stack.isEmpty()) {
            return
        } else {
            throw IllegalArgumentException("Stack is not empty, but symbols is empty.")
        }
    }
}
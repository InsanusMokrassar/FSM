package com.github.insanusmokrassar.FSM.core

import com.github.insanusmokrassar.IObjectK.interfaces.IObject
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject
import java.util.*
import java.util.logging.Logger

class Runner(
        private val states: List<State>,
        private val firstState: Int = 0
) : StateAction, (String) -> Unit {
    @Throws(IllegalArgumentException::class)
    override fun invoke(scope: IObject<Any>, input: String) {
        val stack =  Stack<Int>()
        val inputDeque = ArrayDeque<String>(input.toCharArray().map { it.toString() }.plus(""))
        var stateNum = firstState
        try {
            while (true) {
                val currentState = states[stateNum]
                val currentInput = inputDeque.peek()
                if (currentState.regex.matches(currentInput)) {
                    if (currentState.accept) {
                        inputDeque.pop()
                    }
                    try {
                        currentState.action(scope, currentInput)
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
                } else {
                    if (currentState.error) {
                        throw IllegalArgumentException("Illegal input. " +
                                "Await '${currentState.regex.pattern}' but was '$currentInput' " +
                                "(${input.length - inputDeque.size - 1} symbol)")
                    } else {
                        stateNum++
                    }
                }
            }
        } catch (e: EmptyStackException) {
            if (inputDeque.isEmpty() || (inputDeque.size == 1 && inputDeque.peek().isEmpty())) {
                return
            } else {
                throw IllegalArgumentException("Input completed, but stack is not empty")
            }
        }
    }

    override fun invoke(input: String) {
        invoke(SimpleIObject(), input)
    }
}
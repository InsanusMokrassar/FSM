package com.github.insanusmokrassar.FSM.core

import com.github.insanusmokrassar.IOC.core.getConfig
import com.github.insanusmokrassar.IOC.core.packageKey
import com.github.insanusmokrassar.IOC.utils.extract
import com.github.insanusmokrassar.IObjectK.interfaces.IObject
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject
import java.util.*
import java.util.logging.Logger

val statesField = "states"

val acceptField = "accept"
val errorField = "error"
val stackField = "stack"
val regexField = "regex"
val nextField = "next"
val callbackField = "callback"

/**
 * Await one of next variants:
 * <pre>
 *     {
 *          "states": [
 *              [
 *                  <accept>,
 *                  <error>,
 *                  <stack>,
 *                  <regex as string>,
 *                  <next as Int or null>,
 *                  {
 *                      "package": "package to callback",
 *                      "config": any object as config object.
 *                  }
 *              ],{
 *                  "accept": true/false,
 *                  "error": true/false,
 *                  "stack": true/false,
 *                  "regex": "regex",
 *                  "next": optional int (default == null),
 *                  "callback": {//optional
 *                      "package": "package to callback",
 *                      "config": any object as config object.
 *                  }
 *              }
 *          ]
 *     }
 * </pre>
 */
fun fromConfig(config: IObject<Any>): Runner {
    val states = ArrayList<State>()
    val statesConfig = config.get<List<Any>>(statesField)

    statesConfig.forEach {
        when(it) {
            is List<*> -> states.add(
                    SimpleState(
                            it[0] as Boolean,
                            it[1] as Boolean,
                            it[2] as Boolean,
                            Regex(it[3] as String),
                            when (it[4]) {
                                is Int -> it[4] as Int
                                else -> null
                            },
                            if (it.size > 5) {
                                extract(
                                        (it[5] as IObject<Any>).get(packageKey),
                                        *getConfig(it[5] as IObject<Any>)
                                )
                            } else {
                                defaultAction
                            }
                    )
            )
            is IObject<*> -> (it as? IObject<Any>)?.let {
                    states.add(
                            SimpleState(
                                    if (it.keys().contains(acceptField)) {
                                        it.get(acceptField)
                                    } else {
                                        false
                                    },
                                    if (it.keys().contains(errorField)) {
                                        it.get(errorField)
                                    } else {
                                        false
                                    },
                                    if (it.keys().contains(stackField)) {
                                        it.get(stackField)
                                    } else {
                                        false
                                    },
                                    Regex(it.get(regexField)),
                                    if (it.keys().contains(nextField)) {
                                        it.get(nextField)
                                    } else {
                                        null
                                    },
                                    if(it.keys().contains(callbackField)) {
                                        val callbackConfig = it.get<IObject<Any>>(callbackField)
                                        extract(
                                                callbackConfig.get(packageKey),
                                                *getConfig(callbackConfig)
                                        )
                                    } else {
                                        defaultAction
                                    }
                            )
                    )
            }
        }
    }


    return Runner(states)
}

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
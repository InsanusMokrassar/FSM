package com.github.insanusmokrassar.FSM

import com.github.insanusmokrassar.FSM.core.*
import java.util.*

fun main(args: Array<String>) {
    val runner = Runner(
            listOf(
                    ErrorState("\\d", 3),
                    ClearState("\\d", 5),
                    ErrorState(";", 7),
                    StackErrorState("\\d", 1),
                    AcceptErrorReturnState(";"),
                    AcceptErrorState("\\d", 6, { println("read $it") }),
                    ErrorState("[\\d;]", 1),
                    ErrorReturnState(";")
            )
    )
    val scanner = Scanner(System.`in`)
    while (true) {
        try {
            runner(scanner.nextLine())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

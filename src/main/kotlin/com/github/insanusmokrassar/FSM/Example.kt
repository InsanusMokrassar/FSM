package com.github.insanusmokrassar.FSM

import com.github.insanusmokrassar.FSM.core.*
import com.github.insanusmokrassar.IObjectK.interfaces.has
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject
import java.util.*

fun main(args: Array<String>) {

    val runner = if (args.isNotEmpty()) {
        args.forEach {
            println(it)
        }
        return
    } else {
        val wordField = "word"
        Runner(
                listOf(
                        ErrorState("\\d", 3),
                        ClearState("\\d", 5),
                        ErrorState(";", 7),
                        StackErrorState("\\d", 1),
                        AcceptErrorReturnState(";"),
                        AcceptErrorState("\\d", 6, {
                            scope, input ->
                            val oldField = if (scope.has(wordField)) {
                                scope.get(wordField)
                            } else {
                                ""
                            }
                            scope.put(
                                    wordField,
                                    "$oldField$input"
                            )
                            println("Read $input; Word: ${scope.get<String>(wordField)}")
                        }),
                        ErrorState("[\\d;]", 1),
                        ErrorReturnState(";")
                )
        )
    }
    val scanner = Scanner(System.`in`)
    while (true) {
        try {
            runner(scanner.nextLine())
            println("All right")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

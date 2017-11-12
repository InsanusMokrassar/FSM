package com.github.insanusmokrassar.FSM

import com.github.insanusmokrassar.FSM.core.*
import com.github.insanusmokrassar.FSM.extensions.createRunnerFromConfig
import com.github.insanusmokrassar.IObjectK.interfaces.has
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject
import com.github.insanusmokrassar.IObjectKRealisations.JSONIObject
import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val runner = if (args.isNotEmpty() && (args[0] == "-i" || args[0] == "--interactive")) {
        var config = ""
        while (!config.endsWith("\n\n")) {
            config += "${scanner.nextLine()}\n"
        }
        createRunnerFromConfig(JSONIObject(config))
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
    while (true) {
        try {
            val state = SimpleIObject()
            runner(state, scanner.nextLine())
            println("All right, final state:\n$state")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

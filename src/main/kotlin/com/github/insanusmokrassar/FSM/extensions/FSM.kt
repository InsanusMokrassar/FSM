package com.github.insanusmokrassar.FSM.extensions

import com.github.insanusmokrassar.FSM.core.Runner
import com.github.insanusmokrassar.FSM.core.SimpleState
import com.github.insanusmokrassar.FSM.core.State
import com.github.insanusmokrassar.FSM.core.defaultAction
import com.github.insanusmokrassar.IOC.core.getConfig
import com.github.insanusmokrassar.IOC.core.packageKey
import com.github.insanusmokrassar.IOC.utils.extract
import com.github.insanusmokrassar.IObjectK.interfaces.IObject
import com.github.insanusmokrassar.IObjectK.interfaces.has
import com.github.insanusmokrassar.IObjectK.realisations.SimpleIObject

val statesField = "states"

val markField = "mark"

val acceptField = "accept"
val errorField = "error"
val stackField = "stack"
val regexField = "regex"
val nextField = "next"
val callbackField = "callback"

/**
 * Use with caution - this method is not add config field for callback
 */
fun State.toConfigString(): String {
    return "[$accept,$error,$stack,\"${regex.pattern.replace("\\", "\\\\")}\",$next${if (action != defaultAction) ",{\"$callbackField\":${action::class.java.canonicalName}}" else ""}]"
}

fun State.toConfigObject(mark: String? = null): IObject<Any> {
    val preMap = mutableMapOf<String, Any>()
    preMap.put(acceptField, accept)
    preMap.put(errorField, error)
    preMap.put(stackField, stack)
    preMap.put(regexField, regex.pattern)
    preMap.put(nextField, next.toString())
    if (action != defaultAction) {
        val actionObject = SimpleIObject()
        actionObject.put(packageKey, action::class.java.canonicalName)
        preMap.put(callbackField, actionObject)
    }
    mark?. let {
        preMap.put(markField, it)
    }
    return SimpleIObject(preMap)
}

/**
 * Await:
 * <pre>
 * {
 *     "accept": true/false,
 *     "error": true/false,
 *     "stack": true/false,
 *     "regex": "regex",
 *     "next": optional int (default == null),
 *     "callback": {//optional
 *         "package": "package to callback",
 *         "config": any object as config object.
 *     }
 * }
 * </pre>
 */
fun IObject<Any>.extractState(): State {
    return SimpleState(
            if (has(acceptField)) {
                get(acceptField)
            } else {
                false
            },
            if (has(errorField)) {
                get(errorField)
            } else {
                false
            },
            if (has(stackField)) {
                get(stackField)
            } else {
                false
            },
            Regex(get(regexField)),
            if (has(nextField)) {
                get(nextField)
            } else {
                null
            },
            if(has(callbackField)) {
                val callbackConfig = get<IObject<Any>>(callbackField)
                extract(
                        callbackConfig.get(packageKey),
                        *getConfig(callbackConfig)
                )
            } else {
                defaultAction
            }
    )
}

/**
 * Await:
 * <pre>
 * [
 *     <accept>,
 *     <error>,
 *     <stack>,
 *     <regex as string>,
 *     <next as Int or null>,
 *     {
 *         "package": "package to callback",
 *         "config": any object as config object.
 *     }
 * ]
 * </pre>
 */
fun List<Any>.extractState(): State {
    return SimpleState(
            get(0) as Boolean,
            get(1) as Boolean,
            get(2) as Boolean,
            Regex(get(3) as String),
            when (get(4)) {
                is Int -> get(4) as Int
                else -> null
            },
            if (size > 5) {
                extract(
                        (get(5) as IObject<Any>).get(packageKey),
                        *getConfig(get(5) as IObject<Any>)
                )
            } else {
                defaultAction
            }
    )
}

/**
 * Await:
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
fun createRunnerFromConfig(config: IObject<Any>): Runner {
    return createRunnerFromStatesList(
            config.get(
                    statesField
            )
    )
}

/**
 * Await:
 * <pre>
 * [
 *     [
 *         <accept>,
 *         <error>,
 *         <stack>,
 *         <regex as string>,
 *         <next as Int or null>,
 *         {
 *             "package": "package to callback",
 *             "config": any object as config object.
 *         }
 *     ],{
 *         "accept": true/false,
 *         "error": true/false,
 *         "stack": true/false,
 *         "regex": "regex",
 *         "next": optional int (default == null),
 *         "callback": {//optional
 *             "package": "package to callback",
 *             "config": any object as config object.
 *         }
 *     }
 * ]
 * </pre>
 */
fun createRunnerFromStatesList(statesConfigs: List<Any>): Runner {
    val states = mutableListOf<State>()

    statesConfigs.forEach {
        when(it) {
            is List<*> -> (it as? List<Any>) ?. let {
       states.add(
                        it.extractState()
                )
            }
            is IObject<*> -> (it as? IObject<Any>)?.let {
                states.add(it.extractState())
            }
        }
    }

    return Runner(states)
}



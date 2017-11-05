package com.github.insanusmokrassar.FSM.core

import com.github.insanusmokrassar.IObjectK.interfaces.IObject

interface State {
    val accept: Boolean
    val error: Boolean
    val stack: Boolean
    val regex: Regex
    val next: Int?
    val action: (IObject<Any>, String) -> Unit
}

/**
 * Use with caution - this method is not add config field for callback
 */
fun State.toConfigString(): String {
    return "[$accept,$error,$stack,\"${regex.pattern.replace("\\", "\\\\")}\",$next${if (action != defaultAction) ",{\"$callbackField\":${action::class.java.canonicalName}}" else ""}]"
}

val defaultAction: (IObject<Any>, String) -> Unit = {
    _, _ ->
}

open class SimpleState(
        override val accept: Boolean,
        override val error: Boolean,
        override val stack: Boolean,
        override val regex: Regex,
        override val next: Int?,
        override val action: (IObject<Any>, String) -> Unit = defaultAction
) : State

class AcceptErrorReturnState(regex: String, override val action: (IObject<Any>, String) -> Unit = defaultAction): State {
    override val accept: Boolean = true
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
    override val next: Int? = null
}

class ErrorState(regex: String, override val next: Int, override val action: (IObject<Any>, String) -> Unit = defaultAction): State {
    override val accept: Boolean = false
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
}

class AcceptErrorState(regex: String, override val next: Int, override val action: (IObject<Any>, String) -> Unit = defaultAction): State {
    override val accept: Boolean = true
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
}

class ErrorReturnState(regex: String, override val action: (IObject<Any>, String) -> Unit = defaultAction): State {
    override val accept: Boolean = false
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
    override val next: Int? = null
}

class StackErrorState(regex: String, override val next: Int, override val action: (IObject<Any>, String) -> Unit = defaultAction): State {
    override val accept: Boolean = false
    override val error: Boolean = true
    override val stack: Boolean = true
    override val regex: Regex = Regex(regex)
}

class ClearState(regex: String, override val next: Int, override val action: (IObject<Any>, String) -> Unit = defaultAction): State {
    override val accept: Boolean = false
    override val error: Boolean = false
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
}

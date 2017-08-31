package com.github.insanusmokrassar.FSM.core

interface State {
    val accept: Boolean
    val error: Boolean
    val stack: Boolean
    val regex: Regex
    val next: Int?
    val action: (String) -> Unit
}

open class SimpleState(
        override val accept: Boolean,
        override val error: Boolean,
        override val stack: Boolean,
        override val regex: Regex,
        override val next: Int?,
        override val action: (String) -> Unit = {}
) : State

class AcceptErrorReturnState(regex: String, override val action: (String) -> Unit = {}): State {
    override val accept: Boolean = true
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
    override val next: Int? = null
}

class ErrorState(regex: String, override val next: Int, override val action: (String) -> Unit = {}): State {
    override val accept: Boolean = false
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
}

class AcceptErrorState(regex: String, override val next: Int, override val action: (String) -> Unit = {}): State {
    override val accept: Boolean = true
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
}

class ErrorReturnState(regex: String, override val action: (String) -> Unit = {}): State {
    override val accept: Boolean = false
    override val error: Boolean = true
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
    override val next: Int? = null
}

class StackErrorState(regex: String, override val next: Int, override val action: (String) -> Unit = {}): State {
    override val accept: Boolean = false
    override val error: Boolean = true
    override val stack: Boolean = true
    override val regex: Regex = Regex(regex)
}

class ClearState(regex: String, override val next: Int, override val action: (String) -> Unit = {}): State {
    override val accept: Boolean = false
    override val error: Boolean = false
    override val stack: Boolean = false
    override val regex: Regex = Regex(regex)
}

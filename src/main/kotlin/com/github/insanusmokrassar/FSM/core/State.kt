package com.github.insanusmokrassar.FSM.core

interface State {
    val accept: Boolean
    val error: Boolean
    val stack: Boolean
    val returnFlag: Boolean
    val ruleSymbols: Regex
    val next: Int?
    val action: (String) -> Unit
}
package com.prexoft.core

import androidx.compose.ui.Modifier

fun Modifier.classname(className: String): Modifier {
    var result = this
    className
        .trim()
        .split("\\s+".toRegex())
        .forEach { token ->
            result = result.then(parseToken(token))
        }
    return result
}
package com.prexoft.core

import androidx.compose.ui.Modifier

internal fun parseToken(token: String): Modifier {
    return Rules.parse(token)
}
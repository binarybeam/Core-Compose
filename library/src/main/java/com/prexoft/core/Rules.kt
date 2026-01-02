package com.prexoft.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal object Rules {

    private const val SPACE_UNIT = 4
    private const val RADIUS_UNIT = 4

    fun parse(token: String): Modifier {
        return when {

            /* ---------- PADDING ---------- */

            token.startsWith("p-") ->
                parseSpace(token.removePrefix("p-"))
                    ?.let { Modifier.padding(it) } ?: Modifier

            token.startsWith("px-") ->
                parseSpace(token.removePrefix("px-"))
                    ?.let { Modifier.padding(horizontal = it) } ?: Modifier

            token.startsWith("py-") ->
                parseSpace(token.removePrefix("py-"))
                    ?.let { Modifier.padding(vertical = it) } ?: Modifier

            token.startsWith("pt-") ->
                parseSpace(token.removePrefix("pt-"))
                    ?.let { Modifier.padding(top = it) } ?: Modifier

            token.startsWith("pb-") ->
                parseSpace(token.removePrefix("pb-"))
                    ?.let { Modifier.padding(bottom = it) } ?: Modifier

            token.startsWith("pl-") ->
                parseSpace(token.removePrefix("pl-"))
                    ?.let { Modifier.padding(start = it) } ?: Modifier

            token.startsWith("pr-") ->
                parseSpace(token.removePrefix("pr-"))
                    ?.let { Modifier.padding(end = it) } ?: Modifier

            /* ---------- MARGIN (offset-based) ---------- */

            token.startsWith("m-") ->
                parseSpace(token.removePrefix("m-"))
                    ?.let { Modifier.offset(it, it) } ?: Modifier

            token.startsWith("mx-") ->
                parseSpace(token.removePrefix("mx-"))
                    ?.let { Modifier.offset(x = it) } ?: Modifier

            token.startsWith("my-") ->
                parseSpace(token.removePrefix("my-"))
                    ?.let { Modifier.offset(y = it) } ?: Modifier

            token.startsWith("mt-") ->
                parseSpace(token.removePrefix("mt-"))
                    ?.let { Modifier.offset(y = it) } ?: Modifier

            token.startsWith("mb-") ->
                parseSpace(token.removePrefix("mb-"))
                    ?.let { Modifier.offset(y = -it) } ?: Modifier

            token.startsWith("ml-") ->
                parseSpace(token.removePrefix("ml-"))
                    ?.let { Modifier.offset(x = it) } ?: Modifier

            token.startsWith("mr-") ->
                parseSpace(token.removePrefix("mr-"))
                    ?.let { Modifier.offset(x = -it) } ?: Modifier

            /* ---------- RADIUS ---------- */

            token.startsWith("r-") -> {
                val value = token.removePrefix("r-")
                extractBracket(value)?.let { raw ->
                    parseRawDp(raw)?.let {
                        Modifier.clip(RoundedCornerShape(it))
                    }
                } ?: value.toIntOrNull()?.let {
                    Modifier.clip(RoundedCornerShape((it * RADIUS_UNIT).dp))
                } ?: Modifier
            }

            /* ---------- BACKGROUND (COLOR + OPACITY) ---------- */

            token.startsWith("bg-") -> {
                val raw = token.removePrefix("bg-")
                parseBgWithOpacity(raw)
                    ?.let { Modifier.background(it) }
                    ?: Modifier
            }

            /* ---------- GRADIENT ---------- */

            token == "bg-gradient-to-r" ->
                Modifier.background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF6366F1), Color(0xFFEC4899))
                    )
                )

            token == "bg-gradient-to-b" ->
                Modifier.background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF6366F1), Color(0xFFEC4899))
                    )
                )

            else -> Modifier
        }
    }

    /* ---------- SPACE HELPERS ---------- */

    private fun parseSpace(value: String): Dp? {
        extractBracket(value)?.let { return parseRawDp(it) }
        return value.toIntOrNull()?.let { (it * SPACE_UNIT).dp }
    }

    private fun extractBracket(value: String): String? =
        if (value.startsWith("[") && value.endsWith("]"))
            value.substring(1, value.length - 1)
        else null

    private fun parseRawDp(raw: String): Dp? =
        when {
            raw.endsWith("dp") -> raw.removeSuffix("dp").toFloatOrNull()?.dp
            raw.toFloatOrNull() != null -> raw.toFloat().dp
            else -> null
        }

    /* ---------- COLOR + OPACITY ---------- */

    private fun parseBgWithOpacity(input: String): Color? {
        val (colorPart, opacityPart) = input.split("/", limit = 2)
            .let { it[0] to it.getOrNull(1) }

        val baseColor =
            extractBracket(colorPart)?.let { parseRawColor(it) }
                ?: parseNamedColor(colorPart)
                ?: return null

        val alpha =
            opacityPart?.toIntOrNull()
                ?.coerceIn(0, 100)
                ?.div(100f)
                ?: 1f

        return baseColor.copy(alpha = alpha)
    }

    private fun parseRawColor(raw: String): Color? {

        if (raw.startsWith("#")) {
            return runCatching {
                Color(android.graphics.Color.parseColor(raw))
            }.getOrNull()
        }

        if (raw.startsWith("rgb")) {
            val values = raw
                .removePrefix("rgb(")
                .removeSuffix(")")
                .split(",")
                .map { it.trim().toIntOrNull() }

            if (values.size == 3 && values.all { it != null && it in 0..255 }) {
                return Color(values[0]!!, values[1]!!, values[2]!!)
            }
        }

        return null
    }

    private fun parseNamedColor(name: String): Color? =
        when (name) {
            "black" -> Color.Black
            "white" -> Color.White
            "red" -> Color.Red
            "blue" -> Color(0xFF3B82F6)
            "green" -> Color(0xFF22C55E)
            "primary" -> Color(0xFF6366F1)
            else -> null
        }
}

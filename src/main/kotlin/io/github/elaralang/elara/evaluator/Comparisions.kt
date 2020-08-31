package io.github.elaralang.elara.evaluator

import io.github.elaralang.elara.exceptions.invalidType
// TODO: Add support for operators on defined types
fun compareGreater(lhs: Any, rhs: Any): Boolean {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs > effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs > effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Could not compare")
}
fun compareGreaterOrEqual(lhs: Any, rhs: Any): Boolean {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs >= effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs >= effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Could not compare")
}

fun compareLesser(lhs: Any, rhs: Any): Boolean {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs < effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs < effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Could not compare")
}
fun compareLesserOrEqual(lhs: Any, rhs: Any): Boolean {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs <= effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs <= effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Compare not defined for given type")
}
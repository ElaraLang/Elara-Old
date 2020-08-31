package io.github.elaralang.elara.evaluator

import io.github.elaralang.elara.exceptions.invalidType

fun operateAdd(lhs: Any, rhs: Any): Number {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs + effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs + effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Add not defined for given type")
}

fun operateSubtract(lhs: Any, rhs: Any): Number {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs - effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs - effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Add not defined for given type")
}

fun operateMultiply(lhs: Any, rhs: Any): Number {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs * effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs.toLong()
            is Long -> rhs
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs * effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Add not defined for given type")
}

fun operateDivide(lhs: Any, rhs: Any): Number {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs / effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs / effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Add not defined for given type")
}

fun operateRemainder(lhs: Any, rhs: Any): Number {
    if (lhs is Double) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs % effectiveRhs
    } else if (lhs is Long) {
        val effectiveRhs = when (rhs) {
            is Double -> rhs
            is Long -> rhs.toDouble()
            else -> invalidType("Number", rhs.javaClass.simpleName, "Could not compare")
        }
        return lhs % effectiveRhs
    }
    invalidType("Number", lhs.javaClass.simpleName, "Add not defined for given type")
}
package io.github.elaralang.elara.evaluator

// TODO:: Temporary - Needs a lot of work while setting up evaluator
class Environment(val parent: Environment? = null) {
    private val bindings = mutableMapOf<String, EnvironmentVariable>()

    private data class EnvironmentVariable(val type: String, var value: Any)

    fun defineVariable( identifier: String, type: String, value: Any) {
        bindings[identifier] = EnvironmentVariable(type, value)
    }
    fun assign( identifier: String, value: Any) {
        bindings[identifier]?.value = value
    }

    operator fun get(identifier: String): Any? {
        return bindings[identifier]?.value ?: parent?.get(identifier)
    }
    fun typeOf(identifier: String): String? {
        return bindings[identifier]?.type
    }
}

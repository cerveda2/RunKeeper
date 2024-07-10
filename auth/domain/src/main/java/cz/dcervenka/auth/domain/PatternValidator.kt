package cz.dcervenka.auth.domain

interface PatternValidator {
    fun matches(values: String): Boolean
}
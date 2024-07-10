package cz.dcervenka.auth.data

import android.util.Patterns
import cz.dcervenka.auth.domain.PatternValidator

object EmailPatternValidator: PatternValidator {

    override fun matches(values: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(values).matches()
    }
}
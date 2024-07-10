package cz.dcervenka.auth.domain

class UserDataValidator(
    private val patternValidator: PatternValidator,
) {

    companion object {
        const val MIN_PASSWORD_LENGTH = 9
    }

    fun isValidEmail(email: String): Boolean {
        return patternValidator.matches(email.trim())
    }

    fun validatePassword(password: String): PasswordValidationState {
        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasDigit = password.any { it.isDigit() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasUpperCase = password.any { it.isUpperCase() }

        return PasswordValidationState(
            hasMinLength = hasMinLength,
            hasNumber = hasDigit,
            hasLowerCaseCharacter = hasLowerCase,
            hasUpperCaseCharacter = hasUpperCase,
        )
    }

}
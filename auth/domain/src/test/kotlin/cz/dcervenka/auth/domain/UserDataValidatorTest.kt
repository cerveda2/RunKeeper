package cz.dcervenka.auth.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UserDataValidatorTest {

    private lateinit var userDataValidator: UserDataValidator

    @BeforeEach
    fun setUp() {
        userDataValidator = UserDataValidator(
            patternValidator = object : PatternValidator {
                override fun matches(values: String): Boolean {
                    return true
                }
            },
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun validatePassword() {
        val password = "Test12345"

        val state = userDataValidator.validatePassword(password)

        assertThat(state.isValid).isTrue()
    }

    @ParameterizedTest
    @CsvSource(
        "Test12345, true",
        "test123456, false",
        "12372, false",
        "aSDFGHJK0, true",
        "TEST09876, false"
    )
    fun validateParametrizedPassword(password: String, expected: Boolean) {
        val state = userDataValidator.validatePassword(password)

        assertThat(state.isValid).isEqualTo(expected)
    }
}
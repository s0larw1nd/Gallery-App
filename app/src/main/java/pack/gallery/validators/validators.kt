package pack.gallery.activities

import com.google.android.material.textfield.TextInputLayout

fun validateUsername(inputLayout: TextInputLayout, input: String,
                     minLen: Int=4, maxLen: Int=20): Boolean {
    val regex = Regex("^[A-Za-z0-9]{4,20}$")
    val regexFull = Regex("^(?=.*[A-Za-z])[A-Za-z0-9]{4,20}$")
    return when {
        input.isEmpty() -> {
            inputLayout.error = "Поле не может быть пустым"
            false
        }
        input.length < minLen -> {
            inputLayout.error = "Минимальная длина: $minLen"
            false
        }
        input.length > maxLen -> {
            inputLayout.error = "Максимальная длина: $maxLen"
            false
        }
        !regex.matches(input) -> {
            inputLayout.error = "Допустимые символы: A-z, 0-9"
            false
        }
        !regexFull.matches(input) -> {
            inputLayout.error = "Требуется как минимум одна буква"
            false
        }
        else -> {
            inputLayout.error = null
            true
        }
    }
}

fun validatePassword(inputLayout: TextInputLayout, input: String,
                     minLen: Int=4, maxLen: Int=20): Boolean {
    val regex = Regex("^[A-Za-z0-9+_.!@#$%^&*()-]+$")
    return when {
        input.isEmpty() -> {
            inputLayout.error = "Поле не может быть пустым"
            false
        }
        input.length < minLen -> {
            inputLayout.error = "Минимальная длина: $minLen"
            false
        }
        input.length > maxLen -> {
            inputLayout.error = "Максимальная длина: $maxLen"
            false
        }
        !regex.matches(input) -> {
            inputLayout.error = "Допустимые символы: A-z, 0-9, +_.!@#$%^&*()-"
            false
        }
        else -> {
            inputLayout.error = null
            true
        }
    }
}
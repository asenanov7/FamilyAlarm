package com.example.familyalarm.utils

import android.content.Context
import com.example.familyalarm.R

fun getErrorMessageFromFirebaseErrorCode(firebaseError: String, context:Context): String {
        return when (firebaseError) {
            "ERROR_INVALID_CUSTOM_TOKEN" -> {"Недействительный пользовательский токен"}
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> {"Пользовательский токен не соответствует ожидаемому"}
            "ERROR_INVALID_CREDENTIAL" -> {"Недействительные учетные данные"}
            "ERROR_USER_DISABLED" -> {"Учетная запись пользователя отключена администратором"}
            "ERROR_OPERATION_NOT_ALLOWED" -> {"Операция не разрешена"}
            "ERROR_EMAIL_ALREADY_IN_USE" -> {"Электронная почта уже используется"}
            "ERROR_INVALID_EMAIL" -> {"Некорректный email"}
            "ERROR_WRONG_PASSWORD" -> {"Недействительные данные для входа"}
            "ERROR_TOO_MANY_REQUESTS" -> {"Слишком много запросов отправлено на сервер"}
            "ERROR_USER_NOT_FOUND" -> {"Пользователь с указанным email не найден"}
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {"Учетная запись уже существует с другими учетными данными"}
            "ERROR_REQUIRES_RECENT_LOGIN" -> {"Требуется повторный вход в систему пользователя"}
            "ERROR_PROVIDER_ALREADY_LINKED" -> {"Учетная запись уже связана с данным провайдером"}
            "ERROR_NO_SUCH_PROVIDER" -> {"Провайдер не поддерживается"}
            "ERROR_INVALID_USER_TOKEN" -> {"Недействительный токен пользователя"}
            "ERROR_NETWORK_REQUEST_FAILED" -> {"Ошибка сети"}
            "ERROR_USER_TOKEN_EXPIRED" -> {"Срок действия токена пользователя истек"}
            "ERROR_INVALID_API_KEY" -> {"Недействительный API-ключ Firebase"}
            "ERROR_USER_MISMATCH" -> {"Предоставленные учетные данные не соответствуют ранее связанным учетным данным"}
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> {"Учетные данные уже используются другой учетной записью"}
            "ERROR_WEAK_PASSWORD" -> {"Пароль недостаточно надежен"}
            "ERROR_APP_NOT_AUTHORIZED" -> {"Приложение не авторизовано для доступа к Firebase Authentication"}
            "ERROR_NO_SIGNED_IN_USER" -> {"Нет вошедшего в систему пользователя."}
            "ERROR_INTERNAL_ERROR" -> {"Внутренняя ошибка сервера"}
            else -> {context.getString(R.string.unknownError)}
        }
    }

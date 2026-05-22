package top.pythagodzilla.courser.network.exception


data class HttpException(
    val code: Int,
    val body: String
) : Exception("HTTP $code: $body")

data class LoginFailureException(
    val code: String,
    val exceptionMessage: String
) : Exception("Login Failed $code: $exceptionMessage")

data class SessionExpiredException(
    val code: Int,
    val exceptionMessage: String,
    val statusCode: Int
) : Exception("Session Expired $code: $exceptionMessage - Status Code: $statusCode")

data class UnknownException(
    val code: Int,
    val exceptionMessage: String
) : Exception("An unknown error occurred $code: $exceptionMessage")

data class StringException(
    val exceptionMessage: String
): Exception(exceptionMessage)
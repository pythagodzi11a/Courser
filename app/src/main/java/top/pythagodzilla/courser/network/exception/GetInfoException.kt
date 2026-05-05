package top.pythagodzilla.courser.network.exception

data class ClientException(
    val exceptionMessage: String
) : Exception(exceptionMessage)
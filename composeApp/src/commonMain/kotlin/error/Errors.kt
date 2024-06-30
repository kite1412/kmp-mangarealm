package error

data class UnableRefreshTokenException(
    override val message: String? = "fail to refresh token"
) : RuntimeException()
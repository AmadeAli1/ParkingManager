package service

data class ApiResponse<T>(
    val message: String,
    val response: T? = null,
) {

}

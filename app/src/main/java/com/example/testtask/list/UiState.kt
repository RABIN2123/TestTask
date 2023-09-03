package com.example.testtask.list

data class UiState(
    val prevPage: Boolean = false,
    val error: ErrorStatus = ErrorStatus.NONE,
    val nextPage: Boolean = true,
    val response: StatusResponse = StatusResponse.NONE
) {
    enum class StatusResponse(val num: Int) {
        NONE(0),
        OK(200),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        TOOMANYREQUESTS(429)
    }
    enum class ErrorStatus() {
        NONE,
        FAIL_CONNECT,
        OTHER
    }
}





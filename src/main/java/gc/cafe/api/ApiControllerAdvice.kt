package gc.cafe.api

import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        BindException::class
    )
    protected fun bindException(e: BindException): ApiResponse<Any?> {
        return ApiResponse.of(
            HttpStatus.BAD_REQUEST,
            e.bindingResult.allErrors[0].defaultMessage!!,
            null
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        IllegalAccessException::class
    )
    protected fun illegalAccessException(e: IllegalAccessException): ApiResponse<Any?> {
        return ApiResponse.of(
            HttpStatus.BAD_REQUEST,
            e.message!!,
            null
        )
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(
        Exception::class
    )
    protected fun exception(e: Exception): ApiResponse<Any?> {
        return ApiResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR,
            e.message?: "알 수 없는 오류가 발생했습니다.",
            null
        )
    }
}

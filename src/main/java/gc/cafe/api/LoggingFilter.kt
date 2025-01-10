package gc.cafe.api

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class LoggingFilter : Filter {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        try {
            // 요청 및 응답을 래퍼로 감싸기
            val httpRequest = request as HttpServletRequest
            val httpResponse = response as HttpServletResponse

            val requestWrapper = ContentCachingRequestWrapper(httpRequest)
            val responseWrapper = ContentCachingResponseWrapper(httpResponse)

            val startTime = System.currentTimeMillis()

            try {
                // 실제 필터 체인 실행
                chain?.doFilter(requestWrapper, responseWrapper)
            } finally {
                logRequestDetails(requestWrapper)
                logResponseDetails(responseWrapper, startTime)

                // 응답 복사 (중요: 반드시 필요)
                responseWrapper.copyBodyToResponse()
            }
        } catch (e: Exception) {
            log.error("Unexpected error in LoggingFilter: ${e.message}", e)
            throw e // 서블릿 컨테이너로 예외를 전파
        }
    }

    private fun logRequestDetails(requestWrapper: ContentCachingRequestWrapper) {
        safeLog {
            val requestLog = StringBuilder()
                .append("\nRequest Details\n")
                .append("URI: ${requestWrapper.requestURI}\n")
                .append("Method: ${requestWrapper.method}\n")
                .append("Headers: ${getHeaders(requestWrapper)}\n")
                .append("Parameters: ${getParameters(requestWrapper)}\n")
                .append("Body: ${truncateBody(getRequestBody(requestWrapper))}")
                .toString()
            requestLog
        }
    }

    private fun logResponseDetails(responseWrapper: ContentCachingResponseWrapper, startTime: Long) {
        safeLog {
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            val responseLog = StringBuilder()
                .append("\nResponse Details\n")
                .append("Status: ${responseWrapper.status}\n")
                .append("Headers: ${getResponseHeaders(responseWrapper)}\n")
                .append("Body: ${truncateBody(getResponseBody(responseWrapper))}\n")
                .append("Duration: ${duration}ms")
                .toString()
            responseLog
        }
    }

    private fun safeLog(logAction: () -> String) {
        try {
            log.info(logAction())
        } catch (e: Exception) {
            log.error("Error during logging: ${e.message}", e)
        }
    }

    private fun getHeaders(request: HttpServletRequest): Map<String, String> {
        return request.headerNames.toList()
            .associateWith { request.getHeader(it) }
    }

    private fun getParameters(request: HttpServletRequest): Map<String, String> {
        return request.parameterMap.mapValues { it.value.firstOrNull() ?: "" }
    }

    private fun getRequestBody(request: ContentCachingRequestWrapper): String {
        val content = request.contentAsByteArray
        if (content.isEmpty()) return ""

        return try {
            String(content, Charsets.UTF_8)
        } catch (e: Exception) {
            "Unable to read body: ${e.message}"
        }
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String {
        val content = response.contentAsByteArray
        if (content.isEmpty()) return ""

        return try {
            String(content, Charsets.UTF_8)
        } catch (e: Exception) {
            "Unable to read body: ${e.message}"
        }
    }

    private fun getResponseHeaders(response: HttpServletResponse): Map<String, String?> {
        return response.headerNames
            .associateWith { response.getHeader(it) }
    }

    private fun truncateBody(body: String, maxLength: Int = 1000): String {
        return if (body.length > maxLength) {
            body.substring(0, maxLength) + "...(truncated)"
        } else {
            body
        }
    }

    override fun init(filterConfig: FilterConfig?) {}
    override fun destroy() {}
}

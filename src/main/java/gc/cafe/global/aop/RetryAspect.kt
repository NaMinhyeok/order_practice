package gc.cafe.global.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Aspect
class RetryAspect {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Around("@annotation(retry)")
    @Throws(Throwable::class)
    fun doRetry(joinPoint: ProceedingJoinPoint, retry: Retry): Any {
        log.info("[retry] {} retry={}", joinPoint.signature, retry)

        val maxRetry = retry.value
        var exceptionHolder: Exception? = null

        for (retryCount in 1..maxRetry) {
            try {
                log.info("[RETRY] 시도횟수/전체 시도 가능 횟수={}/{}", retryCount, maxRetry)
                return joinPoint.proceed()
            } catch (e: Exception) {
                exceptionHolder = e
            }
        }
        throw exceptionHolder!!
    }
}

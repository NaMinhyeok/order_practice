package gc.cafe.global.aop

import lombok.extern.slf4j.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Component
@Aspect
class TraceAspect {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Around("@annotation(Trace)")
    @Throws(Throwable::class)
    fun AssumeExecutionTime(joinPoint: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch()

        stopWatch.start()
        val result = joinPoint.proceed()
        stopWatch.stop()

        val totalTimeMillis = stopWatch.totalTimeMillis

        val signature = joinPoint.signature as MethodSignature
        val methodName = signature.method.name

        log.info("[TRACE] 실행 메서드: {}, 실행시간 = {}ms", methodName, totalTimeMillis)

        return result
    }
}

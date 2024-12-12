package gc.cafe.config

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.task.SyncTaskExecutor

@TestConfiguration
class AsyncTestConfig {
    @Bean
    fun asyncExecutorPostProcessor(): AsyncExecutorPostProcessor {
        return AsyncExecutorPostProcessor()
    }

    class AsyncExecutorPostProcessor : BeanPostProcessor {
        @Throws(BeansException::class)
        override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
            if (beanName == "threadPoolTaskExecutor") {
                return SyncTaskExecutor()
            }
            return bean
        }
    }
}
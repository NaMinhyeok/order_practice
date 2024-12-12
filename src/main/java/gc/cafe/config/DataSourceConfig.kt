package gc.cafe.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import javax.sql.DataSource

@Profile("prod")
@Configuration
class DataSourceConfig {
    @ConfigurationProperties(prefix = "spring.datasource.command.hikari")
    @Bean(name = ["commandDataSource"])
    fun commandDataSource(): DataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @ConfigurationProperties(prefix = "spring.datasource.query.hikari")
    @Bean(name = ["queryDataSource"])
    fun queryDataSource(): DataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean
    fun routingDataSource(
        @Qualifier("commandDataSource") command: DataSource,
        @Qualifier("queryDataSource") query: DataSource
    ): DataSource {
        val routingDataSource = ReplicationRoutingDataSource()

        val dataSourceMap: MutableMap<Any, Any> = HashMap()

        dataSourceMap["command"] = command
        dataSourceMap["query"] = query

        routingDataSource.setTargetDataSources(dataSourceMap)
        routingDataSource.setDefaultTargetDataSource(command)

        return routingDataSource
    }

    @Primary
    @Bean
    fun dataSource(routingDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(routingDataSource)
    }
}
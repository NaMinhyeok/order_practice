package gc.cafe.logging.service

import gc.cafe.logging.domain.SlackMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SlackSender(
    private val restClient: RestClient,
    @Value("\${slack.webhook.url}")
    private val webHookUrl: String
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun sendMessage(message: String) {
        runCatching {
            restClient.post()
                .uri(webHookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(SlackMessage(message))
                .retrieve()
                .toBodilessEntity()
        }.onFailure { e ->
            log.error("Slack 메시지 전송 실패", e)
        }
    }
}
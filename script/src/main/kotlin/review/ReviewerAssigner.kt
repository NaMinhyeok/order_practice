package review

import com.slack.api.Slack
import org.kohsuke.github.GitHub

class ReviewerAssigner(
    private val githubClient: GitHub,
    private val slackClient: Slack,
    private val selectedReviewers: Reviewers
) {

    fun assignReviewer(
        repositoryName: String,
        pullRequestNumber: Int,
    ) {
        val repository = githubClient.getRepository(repositoryName)
        val pullRequest = repository.getPullRequest(pullRequestNumber)

        val candidateReviewers = selectedReviewers.reviewers.map { reviewer ->
            githubClient.getUser(reviewer.githubName)
        }
        pullRequest.requestReviewers(candidateReviewers)

        selectedReviewers.reviewers
            .parallelStream()
            .forEach { reviewer ->
                sendSlackMessage(reviewer.slackUserId, createMessage(pullRequest.title, pullRequest.user.login, pullRequest.htmlUrl.toString()))
            }
    }

    private fun sendSlackMessage(
        slackUserId: String,
        message: String
    ) {
        try {
            slackClient.methods(System.getenv("SLACK_TOKEN"))
                .chatPostMessage { req ->
                    req.channel(slackUserId)
                        .text(message)
                }
        } catch (e: Exception) {
            println("Failed to send a message to Slack: $e")
        }
    }

    private fun createMessage(
        title: String,
        prCreator: String,
        prLink: String
    ): String {
        return """
            새로운 PR 리뷰 요청이 있습니다!🙏🙏
            - PR 제목 : $title
            - PR 담당자 : $prCreator
            - 리뷰어 : ${selectedReviewers.reviewers.joinToString { it.githubName }}
            - PR 링크 : $prLink
        """.trimIndent()
    }
}
package review

import org.kohsuke.github.GitHub

class ReviewerAssigner(
    private val githubClient: GitHub,
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
    }
}
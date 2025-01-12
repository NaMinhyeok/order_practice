import config.GithubConfig
import review.ReviewerAssigner
import review.ReviewerFileReader
import review.Reviewers

class ScriptApplication {
}

fun main() {
    val token = System.getenv("GITHUB_TOKEN")
    val repository = System.getenv("GITHUB_REPOSITORY")
    val prNumber = System.getenv("PR_NUMBER")?.toInt() ?: throw IllegalArgumentException("PR_NUMBER is not found")
    val prCreator = System.getenv("PR_CREATOR") ?: throw IllegalArgumentException("PR_CREATOR is not found")

    val githubClient = GithubConfig().createGithubClient(token)
    val candidateReviewers = ReviewerFileReader().readReviewers()
    val selectReviewers = Reviewers(candidateReviewers).selectReviewers(prCreator)
    ReviewerAssigner(githubClient, selectReviewers).assignReviewer(repository, prNumber)
}
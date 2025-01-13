package review

data class Reviewers(
    val reviewers: List<Reviewer>
) {
    fun selectReviewers(
        prCreator: String
    ): Reviewers {
        return copy(reviewers = reviewers.filter { it.githubName != prCreator })
    }
}

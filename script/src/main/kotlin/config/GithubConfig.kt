package config

import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder

class GithubConfig {
    fun createGithubClient(token: String): GitHub {
        return GitHubBuilder()
            .withOAuthToken(token)
            .build()
    }
}
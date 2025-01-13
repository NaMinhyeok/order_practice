package review

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class ReviewerFileReader {
    fun readReviewers(): List<Reviewer> {
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val configFile = javaClass.classLoader.getResourceAsStream("reviewers.yml")
            ?.bufferedReader()
            ?.readText()
            ?: throw IllegalStateException("Cannot find reviewers.yml")

        return mapper.readValue(configFile, Reviewers::class.java).reviewers
    }
}
package com.example

import com.example.data.model.github.GitHubUserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GitHubDataServiceTest {

    @Test
    fun userProfile_domainModel_correctlyHoldsAttributes() {
        val profile = GitHubUserProfile(
            id = 123456L,
            login = "dextervann1",
            name = "Dexter Lamar Vann",
            avatarUrl = "https://avatars.githubusercontent.com/u/123456",
            htmlUrl = "https://github.com/dextervann1",
            company = "Vann Family Ventures LLC",
            blog = "https://vannfamilyventures.com",
            location = "United States",
            email = "dextervann1@gmail.com",
            bio = "Founder & Architect at ScalenStack Studios",
            twitterUsername = "dextervann1",
            publicRepos = 42,
            publicGists = 10,
            followers = 1337,
            following = 88,
            memberSince = "2021-04-12"
        )

        assertEquals("dextervann1", profile.login)
        assertEquals("Dexter Lamar Vann", profile.name)
        assertEquals(42, profile.publicRepos)
        assertEquals(1337, profile.followers)
        assertNotNull(profile.avatarUrl)
    }
}

package dev.pyrlyn.landing.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SiteConfigTest {
    private fun cfg(vararg env: Pair<String, String>) = SiteConfig.fromEnv { env.toMap()[it] }

    @Test
    fun defaults() {
        val c = cfg()
        assertEquals("https://pyrlyn.dev", c.siteUrl)
        assertEquals("/", c.base)
        assertEquals("pyrlyn", c.algoliaIndex)
        assertEquals("", c.apiUrl)
        assertFalse(c.algoliaEnabled)
        assertTrue(c.deferAnalytics)
        assertTrue(c.gaMeasurementId.startsWith("G-"))
    }

    @Test
    fun baseIsNormalisedToLeadingAndTrailingSlash() {
        listOf("cv" to "/cv/", "/cv" to "/cv/", "/cv/" to "/cv/", "" to "/", "/" to "/", "a/b" to "/a/b/")
            .forEach { (raw, want) -> assertEquals(want, cfg("SITE_BASE" to raw).base, raw) }
    }

    @Test
    fun urlAndAbsJoinBaseAndPath() {
        val c = cfg("SITE_URL" to "https://listepo.dev/", "SITE_BASE" to "/cv/")
        assertEquals("/cv/", c.url())
        assertEquals("/cv/docs/", c.url("docs/"))
        assertEquals("https://listepo.dev/cv/a/", c.abs("/a/"))
        assertEquals("/x/", cfg().url("/x/"))
    }

    @Test
    fun invalidIdsAndApiUrlsAreDropped() {
        val c = cfg("GA_MEASUREMENT_ID" to "off", "POSTHOG_KEY" to "off", "API_URL" to "javascript:alert(1)")
        assertEquals("", c.gaMeasurementId)
        assertEquals("", c.posthogKey)
        assertEquals("", c.apiUrl)
        assertEquals("https://api.pyrlyn.dev", cfg("API_URL" to " https://api.pyrlyn.dev ").apiUrl)
    }

    @Test
    fun perfDeferAnalyticsZeroLoadsAtOnce() {
        assertFalse(cfg("PERF_DEFER_ANALYTICS" to "0").deferAnalytics)
    }
}

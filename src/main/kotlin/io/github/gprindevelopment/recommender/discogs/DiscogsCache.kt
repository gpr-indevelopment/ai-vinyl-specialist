package io.github.gprindevelopment.recommender.discogs

import org.springframework.stereotype.Component

@Component
class DiscogsCache {

    private val cache = mutableMapOf<Int, ReleaseResponse>()

    fun getRelease(releaseId: Int): ReleaseResponse? {
        return cache[releaseId];
    }

    fun store(release: ReleaseResponse) {
        cache[release.id] = release
    }
}
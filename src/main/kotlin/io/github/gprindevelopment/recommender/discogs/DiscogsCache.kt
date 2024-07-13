package io.github.gprindevelopment.recommender.discogs

import org.springframework.stereotype.Component

@Component
class DiscogsCache {

    fun getRelease(releaseId: Int): ReleaseResponse? {
        return null;
    }
}
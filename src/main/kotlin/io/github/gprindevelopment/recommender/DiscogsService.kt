package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Service

@Service
class DiscogsService(
    val discogsClient: DiscogsClient
) {
    fun getFullCollection(user: DiscogsUser): DiscogsCollectionResponse {
        return discogsClient.getCollection(user.username)
    }
}
package io.github.gprindevelopment.recommender

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(url = "https://api.discogs.com", name = "discogs")
interface DiscogsClient {

    @GetMapping("/users/{userId}/collection/folders/0/releases")
    fun getCollection(@PathVariable("userId") userId: String): DiscogsCollectionResponse
}
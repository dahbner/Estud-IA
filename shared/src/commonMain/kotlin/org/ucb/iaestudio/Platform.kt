package org.ucb.iaestudio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
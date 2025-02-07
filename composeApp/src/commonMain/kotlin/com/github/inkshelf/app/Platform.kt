package com.github.inkshelf.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package com.blog.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType

@Service
class Logger {
    companion object {

        fun info(message: String, map: Map<String, Any>?) {
            print("Info: $message ")
            print(map)
            println()
        }

        fun error(message: String, map: Map<String, Any>?) {
            print("Error: $message ")
            print(map)
            println()
        }
    }
}

fun <T> Mono<T>.logOnSuccess(message: String, map: Map<String, Any>? = null): Mono<T> {
    return doOnEach {
        if (it.type == SignalType.ON_NEXT)
            Logger.info(message, map)
    }
}

fun <T> Mono<T>.logOnError(message: String, map: Map<String, Any>? = null): Mono<T> {
    return doOnEach {
        if (it.type == SignalType.ON_ERROR)
            Logger.error(message, map)
    }
}

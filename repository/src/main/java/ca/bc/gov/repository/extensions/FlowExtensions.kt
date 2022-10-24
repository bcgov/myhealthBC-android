package ca.bc.gov.repository.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, R> Flow<List<T>>.mapFlowContent(transform: (T) -> R): Flow<List<R>> =
    this.map { content ->
        content.map {
            transform(it)
        }
    }

package com.example.util.simpletimetracker.presentation.remember

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.time.delay
import java.time.Duration
import java.time.Instant

@Composable
fun rememberDurationSince(epochMillis: Long): Duration {
    var duration by remember { mutableStateOf(durationSince(epochMillis)) }
    LaunchedEffect(duration) {
        delay(Duration.ofSeconds(1L))
        duration = durationSince(epochMillis)
    }
    return duration
}

private fun durationSince(epochMillis: Long): Duration {
    return Duration.between(Instant.ofEpochMilli(epochMillis), Instant.now())
}
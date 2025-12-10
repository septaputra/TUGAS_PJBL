package com.kelasxi.aplikasimonitoringkelas.utils

object GradeUtils {
    fun numberToGradeLetter(nilai: Double): String {
        return when {
            nilai >= 85 -> "A"
            nilai >= 75 -> "B"
            nilai >= 65 -> "C"
            nilai >= 55 -> "D"
            else -> "E"
        }
    }
}
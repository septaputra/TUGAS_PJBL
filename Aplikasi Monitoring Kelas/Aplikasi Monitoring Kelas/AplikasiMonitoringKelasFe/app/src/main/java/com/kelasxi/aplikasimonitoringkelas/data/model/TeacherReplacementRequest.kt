package com.kelasxi.aplikasimonitoringkelas.data.model

import com.google.gson.annotations.SerializedName

data class CreateTeacherReplacementRequest(
    @SerializedName("kelas_kosong_id") val kelas_kosong_id: Int,
    @SerializedName("guru_pengganti_id") val guru_pengganti_id: Int,
    @SerializedName("keterangan") val keterangan: String?
)
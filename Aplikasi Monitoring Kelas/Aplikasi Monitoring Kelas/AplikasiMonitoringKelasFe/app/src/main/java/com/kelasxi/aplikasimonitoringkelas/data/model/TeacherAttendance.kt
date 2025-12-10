package com.kelasxi.aplikasimonitoringkelas.data.model

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class TeacherAttendance(
    @SerializedName("id") val id: Int,
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("guru_id") val guruId: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("jam_masuk") val jamMasuk: String?,
    @SerializedName("status") val status: String, // hadir, telat, tidak_hadir
    @SerializedName("keterangan") val keterangan: String?,
    @JsonAdapter(UserOrIdDeserializer::class)
    @SerializedName("created_by") val createdBy: User?, // Can be User object or just an ID
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("schedule") val schedule: Schedule?,
    @JsonAdapter(GuruOrIdDeserializer::class)
    @SerializedName("guru") val guru: Guru?
)

// Custom deserializer to handle both User object and integer ID
class UserOrIdDeserializer : JsonDeserializer<User?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): User? {
        return when {
            json == null || json.isJsonNull -> null
            json.isJsonPrimitive && json.asJsonPrimitive.isNumber -> {
                // If it's just a number (ID), return null for now
                // Backend should always return full User object
                null
            }
            json.isJsonObject -> {
                // If it's an object, deserialize it as User
                context?.deserialize(json, User::class.java)
            }
            else -> null
        }
    }
}

// Custom deserializer to handle Guru object or integer ID
class GuruOrIdDeserializer : JsonDeserializer<Guru?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Guru? {
        return when {
            json == null || json.isJsonNull -> null
            json.isJsonPrimitive && json.asJsonPrimitive.isNumber -> {
                // If it's just a number (ID), return null for now
                // Backend may return full Guru object if available
                null
            }
            json.isJsonObject -> {
                // If it's an object, deserialize it as Guru
                context?.deserialize(json, Guru::class.java)
            }
            else -> null
        }
    }
}

data class TodayScheduleWithAttendance(
    @SerializedName("schedule") val schedule: Schedule,
    @SerializedName("attendance") val attendance: TeacherAttendance?,
    @SerializedName("has_attendance") val hasAttendance: Boolean,
    @SerializedName("status") val status: String // hadir, telat, tidak_hadir, belum_dicatat
)

data class TodaySchedulesResponse(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("hari") val hari: String,
    @SerializedName("total_schedules") val totalSchedules: Int,
    @SerializedName("sudah_dicatat") val sudahDicatat: Int,
    @SerializedName("belum_dicatat") val belumDicatat: Int,
    @SerializedName("data") val data: List<TodayScheduleWithAttendance>
)

data class TodayAttendanceResponse(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("total") val total: Int,
    @SerializedName("hadir") val hadir: Int,
    @SerializedName("telat") val telat: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    @SerializedName("data") val data: List<TeacherAttendance>
)

data class AttendanceStatistics(
    @SerializedName("total") val total: Int,
    @SerializedName("hadir") val hadir: Int,
    @SerializedName("telat") val telat: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    @SerializedName("percentage_hadir") val percentageHadir: Double,
    @SerializedName("percentage_telat") val percentageTelat: Double
)

data class TeacherAttendanceRequest(
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("guru_id") val guruId: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("jam_masuk") val jamMasuk: String,
    @SerializedName("status") val status: String,
    @SerializedName("keterangan") val keterangan: String?
)

data class TeacherAttendanceUpdateRequest(
    @SerializedName("jam_masuk") val jamMasuk: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("keterangan") val keterangan: String?
)

data class PaginatedResponse<T>(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("data") val data: List<T>,
    @SerializedName("first_page_url") val firstPageUrl: String,
    @SerializedName("from") val from: Int?,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("last_page_url") val lastPageUrl: String,
    @SerializedName("next_page_url") val nextPageUrl: String?,
    @SerializedName("path") val path: String,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("prev_page_url") val prevPageUrl: String?,
    @SerializedName("to") val to: Int?,
    @SerializedName("total") val total: Int
)

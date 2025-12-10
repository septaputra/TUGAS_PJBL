package com.kelasxi.aplikasimonitoringkelas.data.model

// Models untuk Teacher Replacement (Guru Pengganti) - Updated System
data class TeacherReplacementResponse(
    val success: Boolean,
    val message: String,
    val data: List<TeacherReplacement>
)

data class TeacherReplacement(
    val id: Int,
    val schedule_id: Int,
    val guru_pengganti: Guru,
    val guru_asli: Guru?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?,
    val assigned_by: User?,
    val created_at: String,
    val updated_at: String
)

data class AssignReplacementRequest(
    val attendance_id: Int,
    val guru_pengganti_id: Int,
    val keterangan: String?
)

// Models untuk Guru Pengganti (OLD - Keep for backward compatibility)
data class GuruPenggantiResponse(
    val success: Boolean,
    val message: String,
    val data: List<GuruPengganti>
)

data class GuruPengganti(
    val id: Int,
    val guru_pengganti_id: Int,
    val guru_asli_id: Int?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?,
    val assigned_by: Int,
    val guruPengganti: Guru,
    val guruAsli: Guru?,
    val created_at: String,
    val updated_at: String
)

data class GuruPenggantiRequest(
    val guru_pengganti_id: Int,
    val guru_asli_id: Int?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?
)

// Models untuk Kelas Kosong
data class KelasKosongResponse(
    val success: Boolean,
    val message: String,
    val data: List<KelasKosong>,
    val summary: KelasKosongSummary
)

data class KelasKosong(
    val jadwal_id: Int?,
    val attendance_id: Int?, // ID dari teacher_attendance
    val kelas: String,
    val mata_pelajaran: String,
    val guru: Guru,
    val jam_mulai: String?,
    val jam_selesai: String?,
    val ruang: String?,
    val tanggal: String,
    val hari: String,
    val status: String, // "Tidak Hadir"
    val keterangan: String? // Keterangan dari teacher attendance
)

data class KelasKosongSummary(
    val total_jadwal: Int,
    val total_kelas_kosong: Int,
    val tanggal: String,
    val hari: String
)

// Models for Replacement Form (student-facing)
data class ReplacementFormResponse(
    val success: Boolean,
    val message: String,
    val data: ReplacementFormData?
)

data class ReplacementFormData(
    val attendance: TeacherAttendance,
    val session: SessionData?,
    val candidates: CandidateGroups
)

data class SessionData(
    val tanggal: String?,
    val hari: String?,
    val jam_mulai: String?,
    val mata_pelajaran: String?,
    val kelas: String?
)

data class CandidateGroups(
    val available: List<CandidateItem>,
    val conflicting: List<CandidateItem>
)

data class CandidateItem(
    val id: Int,
    val name: String,
    val mata_pelajaran: String?
)

// Response for replace action
data class ReplaceTeacherResponse(
    val success: Boolean,
    val message: String,
    val data: ReplaceResultData?
)

data class ReplaceResultData(
    val attendance: TeacherAttendance?,
    val proposal: GuruPengganti?,
    val kehadiran_guru: Map<String, Any>?
)

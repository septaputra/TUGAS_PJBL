package com.kelasxi.aplikasimonitoringkelas.data.model

data class Assignment(
    val id: Int,
    val guru_id: Int,
    val kelas: String,
    val mata_pelajaran: String,
    val judul: String,
    val deskripsi: String,
    val deadline: String,
    val file_path: String?,
    val tipe: String,  // "tugas", "ulangan", "ujian"
    val bobot: Int,
    val created_at: String,
    val guru: GuruInfo?,
    // Extra fields for siswa
    val is_submitted: Boolean? = null,
    val submission_status: String? = null,  // "pending", "late", "graded"
    val submitted_at: String? = null,
    // Extra fields for guru
    val total_submissions: Int? = null,
    val graded_count: Int? = null
)

data class GuruInfo(
    val id: Int,
    val name: String,
    val email: String
)

data class AssignmentSubmission(
    val id: Int,
    val assignment_id: Int,
    val siswa_id: Int,
    val file_path: String?,
    val keterangan: String?,
    val status: String,  // "pending", "late", "graded"
    val submitted_at: String,
    val siswa: SiswaInfo?,
    val assignment: Assignment?,
    val grade: Grade?
)

data class SiswaInfo(
    val id: Int,
    val name: String,
    val email: String
)

data class Grade(
    val id: Int,
    val siswa_id: Int,
    val assignment_id: Int,
    val guru_id: Int,
    val nilai: Double,
    val catatan: String?,
    val created_at: String,
    val siswa: SiswaInfo?,
    val assignment: Assignment?,
    val guru: GuruInfo?
)

// Response models
data class AssignmentsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Assignment>
)

data class AssignmentResponse(
    val success: Boolean,
    val message: String,
    val data: Assignment
)

data class SubmissionsResponse(
    val success: Boolean,
    val message: String,
    val data: List<AssignmentSubmission>
)

data class SubmissionResponse(
    val success: Boolean,
    val message: String,
    val data: AssignmentSubmission
)

data class GradesResponse(
    val success: Boolean,
    val message: String,
    val data: List<Grade>
)

data class GradeResponse(
    val success: Boolean,
    val message: String,
    val data: Grade
)

data class SiswaGradesResponse(
    val success: Boolean,
    val message: String,
    val data: SiswaGradesData
)

data class SiswaGradesData(
    val grades: List<Grade>,
    val statistics: GradeStatistics
)

data class GradeStatistics(
    val total_assignments: Int,
    val average_grade: Double?,
    val highest_grade: Double?,
    val lowest_grade: Double?
)

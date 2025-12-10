package com.kelasxi.aplikasimonitoringkelas.data.api

import com.kelasxi.aplikasimonitoringkelas.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Any>>
    
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): Response<LoginResponse>
    
    // Jadwal Pelajaran (Semua Role)
    @GET("jadwal")
    suspend fun getSchedules(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null,
        @Query("kelas") kelas: String? = null
    ): Response<ScheduleResponse>
    
    // ==================== ADMIN: User Management ====================
    
    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<UsersResponse>
    
    @GET("users/guru")
    suspend fun getGuruList(@Header("Authorization") token: String): Response<UsersResponse>
    
    @GET("teachers")  // New endpoint for fetching from teachers table
    suspend fun getTeachers(@Header("Authorization") token: String): Response<UsersResponse>
    
    @POST("users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body request: CreateUserRequest
    ): Response<ApiResponse<User>>
    
    @PUT("users/{id}/role")
    suspend fun updateUserRole(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UpdateRoleRequest
    ): Response<ApiResponse<User>>
    
    @PUT("users/{id}/ban")
    suspend fun banUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<ApiResponse<User>>
    
    @PUT("users/{id}/unban")
    suspend fun unbanUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<ApiResponse<User>>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<ApiResponse<Any>>
    
    // ==================== ADMIN: Jadwal Management ====================
    
    @POST("jadwal")
    suspend fun createSchedule(
        @Header("Authorization") token: String,
        @Body request: ScheduleRequest
    ): Response<ApiResponse<Schedule>>
    
    @PUT("jadwal/{id}")
    suspend fun updateSchedule(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: ScheduleRequest
    ): Response<ApiResponse<Schedule>>
    
    @DELETE("jadwal/{id}")
    suspend fun deleteSchedule(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>
    
    // ==================== SISWA: Monitoring ====================
    
    @POST("monitoring/store")
    suspend fun storeMonitoring(
        @Header("Authorization") token: String,
        @Body request: MonitoringRequest
    ): Response<MonitoringResponse>
    
    @GET("monitoring/my-reports")
    suspend fun getMyReports(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): Response<MonitoringListResponse>
    
    // ==================== KURIKULUM & KEPALA SEKOLAH: Monitoring ====================
    
    @GET("monitoring")
    suspend fun getMonitoring(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas") kelas: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<MonitoringListResponse>
    
    @GET("monitoring/empty-class-reports")
    suspend fun getEmptyClassReports(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas") kelas: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<MonitoringListResponse>
    
    @GET("monitoring/empty-classes-only")
    suspend fun getEmptyClassesOnly(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas") kelas: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<MonitoringListResponse>
    
    @GET("monitoring/kelas-kosong")
    suspend fun getKelasKosong(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): Response<KelasKosongResponse>
    
    @GET("monitoring/kelas-kosong-attendance")
    suspend fun getKelasKosongFromAttendance(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas") kelas: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<KelasKosongResponse>
    
    // ==================== KURIKULUM: Guru Pengganti ====================
    
    @GET("guru-pengganti")
    suspend fun getGuruPengganti(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas") kelas: String? = null
    ): Response<GuruPenggantiResponse>
    
    @POST("guru-pengganti")
    suspend fun createGuruPengganti(
        @Header("Authorization") token: String,
        @Body request: GuruPenggantiRequest
    ): Response<ApiResponse<GuruPengganti>>
    
    @PUT("guru-pengganti/{id}")
    suspend fun updateGuruPengganti(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: GuruPenggantiRequest
    ): Response<ApiResponse<GuruPengganti>>
    
    @DELETE("guru-pengganti/{id}")
    suspend fun deleteGuruPengganti(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>
    
    // ==================== KURIKULUM: Teacher Replacement (New System) ====================
    
    @GET("teacher-replacement")
    suspend fun getTeacherReplacements(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): Response<TeacherReplacementResponse>
    
    @POST("teacher-replacement/assign")
    suspend fun assignReplacement(
        @Header("Authorization") token: String,
        @Body request: AssignReplacementRequest
    ): Response<ApiResponse<TeacherReplacement>>
    
    @POST("teacher-replacement/{id}/cancel")
    suspend fun cancelReplacement(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>
    
    // ==================== KURIKULUM: Teacher Attendance ====================
    
    @GET("teacher-attendance/today-schedules")
    suspend fun getTodaySchedules(
        @Header("Authorization") token: String
    ): Response<TodaySchedulesResponse>
    
    @GET("teacher-attendance/all-schedules")
    suspend fun getAllSchedules(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas") kelas: String? = null
    ): Response<TodaySchedulesResponse>
    
    @GET("teacher-attendance/today")
    suspend fun getTodayAttendance(
        @Header("Authorization") token: String
    ): Response<TodayAttendanceResponse>
    
    @GET("teacher-attendance/statistics")
    suspend fun getAttendanceStatistics(
        @Header("Authorization") token: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<AttendanceStatistics>
    
    @GET("teacher-attendance")
    suspend fun getTeacherAttendances(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("guru_id") guruId: Int? = null,
        @Query("status") status: String? = null,
        @Query("kelas") kelas: String? = null,
        @Query("mata_pelajaran") mataPelajaran: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<PaginatedResponse<TeacherAttendance>>
    
    @POST("teacher-attendance")
    suspend fun createTeacherAttendance(
        @Header("Authorization") token: String,
        @Body request: TeacherAttendanceRequest
    ): Response<ApiResponse<TeacherAttendance>>
    
    @GET("teacher-attendance/{id}")
    suspend fun getTeacherAttendance(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<TeacherAttendance>

    // Endpoint for student-facing replacement form (candidates + session info)
    @GET("teacher-attendance/{id}/replacement-form")
    suspend fun getReplacementForm(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ReplacementFormResponse>

    // Endpoint to perform replacement for an attendance
    @POST("teacher-attendance/{id}/replace")
    suspend fun replaceTeacher(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: AssignReplacementRequest
    ): Response<ApiResponse<ReplaceResultData>>
    
    @PUT("teacher-attendance/{id}")
    suspend fun updateTeacherAttendance(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: TeacherAttendanceUpdateRequest
    ): Response<ApiResponse<TeacherAttendance>>
    
    @DELETE("teacher-attendance/{id}")
    suspend fun deleteTeacherAttendance(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>
}

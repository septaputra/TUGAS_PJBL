<?php

namespace App\Http\Controllers;

use App\Models\Grade;
use App\Models\Assignment;
use App\Models\AssignmentSubmission;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Symfony\Component\HttpFoundation\Response;

class GradeController extends Controller
{
    /**
     * Get grades (siswa: own grades, guru: grades they created)
     */
    public function index(Request $request)
    {
        try {
            $query = Grade::with(['siswa:id,name,email', 'assignment', 'guru:id,name,email']);

            // Siswa hanya bisa lihat nilai sendiri
            if ($request->user()->role === 'siswa') {
                $query->where('siswa_id', $request->user()->id);
            }

            // Guru atau kurikulum hanya bisa lihat nilai yang dia buat
            if (in_array($request->user()->role, ['guru', 'kurikulum'])) {
                $query->where('guru_id', $request->user()->id);
            }

            // Filter by mata pelajaran
            if ($request->has('mata_pelajaran')) {
                $query->whereHas('assignment', function ($q) use ($request) {
                    $q->where('mata_pelajaran', $request->mata_pelajaran);
                });
            }

            // Filter by kelas
            if ($request->has('kelas')) {
                $query->whereHas('assignment', function ($q) use ($request) {
                    $q->where('kelas', $request->kelas);
                });
            }

            $grades = $query->orderBy('created_at', 'desc')->get();

            return response()->json([
                'success' => true,
                'message' => 'Grades retrieved successfully',
                'data' => $grades
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to retrieve grades',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get grades for specific siswa
     */
    public function getSiswaGrades($siswaId, Request $request)
    {
        try {
            // Siswa hanya bisa lihat nilai sendiri
            if ($request->user()->role === 'siswa' && $request->user()->id != $siswaId) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to view this student\'s grades'
                ], Response::HTTP_FORBIDDEN);
            }

            $grades = Grade::where('siswa_id', $siswaId)
                ->with(['assignment', 'guru:id,name,email'])
                ->orderBy('created_at', 'desc')
                ->get();

            // Hitung statistik
            $stats = [
                'total_assignments' => $grades->count(),
                'average_grade' => $grades->avg('nilai'),
                'highest_grade' => $grades->max('nilai'),
                'lowest_grade' => $grades->min('nilai'),
            ];

            return response()->json([
                'success' => true,
                'message' => 'Student grades retrieved successfully',
                'data' => [
                    'grades' => $grades,
                    'statistics' => $stats
                ]
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to retrieve student grades',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get grades by kelas
     */
    public function getKelasgrades($kelas, Request $request)
    {
        try {
            // Only guru, admin, and kepala_sekolah can view class grades
            if (!in_array($request->user()->role, ['guru', 'admin', 'kepala_sekolah'])) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to view class grades'
                ], Response::HTTP_FORBIDDEN);
            }

            $grades = Grade::whereHas('assignment', function ($q) use ($kelas) {
                    $q->where('kelas', $kelas);
                })
                ->with(['siswa:id,name,email', 'assignment'])
                ->orderBy('created_at', 'desc')
                ->get();

            // Group by siswa
            $groupedGrades = $grades->groupBy('siswa_id')->map(function ($siswaGrades) {
                return [
                    'siswa' => $siswaGrades->first()->siswa,
                    'grades' => $siswaGrades,
                    'average' => $siswaGrades->avg('nilai'),
                    'total_assignments' => $siswaGrades->count()
                ];
            })->values();

            return response()->json([
                'success' => true,
                'message' => 'Class grades retrieved successfully',
                'data' => $groupedGrades
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to retrieve class grades',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Store new grade (Guru only)
     */
    public function store(Request $request)
    {
        try {
            $validator = Validator::make($request->all(), [
                'siswa_id' => 'required|exists:users,id',
                'assignment_id' => 'required|exists:assignments,id',
                'nilai' => 'required|numeric|min:0|max:100',
                'catatan' => 'nullable|string'
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation failed',
                    'errors' => $validator->errors()
                ], Response::HTTP_UNPROCESSABLE_ENTITY);
            }

            // Check if assignment belongs to this guru
            $assignment = Assignment::findOrFail($request->assignment_id);
            if ($assignment->guru_id !== $request->user()->id && $request->user()->role !== 'admin' && $request->user()->role !== 'kepala_sekolah') {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to grade this assignment'
                ], Response::HTTP_FORBIDDEN);
            }

            // Check if siswa has submitted the assignment
            $submission = AssignmentSubmission::where('assignment_id', $request->assignment_id)
                ->where('siswa_id', $request->siswa_id)
                ->first();

            if (!$submission) {
                return response()->json([
                    'success' => false,
                    'message' => 'Student has not submitted this assignment yet'
                ], Response::HTTP_CONFLICT);
            }

            // Check if grade already exists
            $existingGrade = Grade::where('siswa_id', $request->siswa_id)
                ->where('assignment_id', $request->assignment_id)
                ->first();

            if ($existingGrade) {
                return response()->json([
                    'success' => false,
                    'message' => 'Grade already exists for this student and assignment. Use update instead.'
                ], Response::HTTP_CONFLICT);
            }

            $grade = Grade::create([
                'siswa_id' => $request->siswa_id,
                'assignment_id' => $request->assignment_id,
                'guru_id' => $request->user()->id,
                'nilai' => $request->nilai,
                'catatan' => $request->catatan
            ]);

            // Update submission status to graded
            $submission->update(['status' => 'graded']);

            return response()->json([
                'success' => true,
                'message' => 'Grade created successfully',
                'data' => $grade->load(['siswa:id,name,email', 'assignment', 'guru:id,name,email'])
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to create grade',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update grade (Guru only)
     */
    public function update(Request $request, $id)
    {
        try {
            $grade = Grade::findOrFail($id);

            // Check if guru owns this grade
            if ($grade->guru_id !== $request->user()->id && $request->user()->role !== 'admin' && $request->user()->role !== 'kepala_sekolah') {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to update this grade'
                ], Response::HTTP_FORBIDDEN);
            }

            $validator = Validator::make($request->all(), [
                'nilai' => 'required|numeric|min:0|max:100',
                'catatan' => 'nullable|string'
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation failed',
                    'errors' => $validator->errors()
                ], Response::HTTP_UNPROCESSABLE_ENTITY);
            }

            $grade->update($request->only(['nilai', 'catatan']));

            return response()->json([
                'success' => true,
                'message' => 'Grade updated successfully',
                'data' => $grade->load(['siswa:id,name,email', 'assignment', 'guru:id,name,email'])
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to update grade',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete grade (Guru only)
     */
    public function destroy($id, Request $request)
    {
        try {
            $grade = Grade::findOrFail($id);

            // Check if guru owns this grade
            if ($grade->guru_id !== $request->user()->id && $request->user()->role !== 'admin' && $request->user()->role !== 'kepala_sekolah') {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to delete this grade'
                ], Response::HTTP_FORBIDDEN);
            }

            // Update submission status back to pending/late
            $submission = AssignmentSubmission::where('assignment_id', $grade->assignment_id)
                ->where('siswa_id', $grade->siswa_id)
                ->first();

            if ($submission) {
                $submission->update([
                    'status' => $submission->isLate() ? 'late' : 'pending'
                ]);
            }

            $grade->delete();

            return response()->json([
                'success' => true,
                'message' => 'Grade deleted successfully'
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to delete grade',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}

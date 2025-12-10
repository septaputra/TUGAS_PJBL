<?php

namespace App\Http\Controllers;

use App\Models\Assignment;
use App\Models\AssignmentSubmission;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Validator;
use Symfony\Component\HttpFoundation\Response;

class AssignmentController extends Controller
{
    /**
     * Get all assignments (dengan filter optional)
     */
    public function index(Request $request)
    {
        try {
            $query = Assignment::with(['guru:id,name,email']);

            // Filter by kelas
            if ($request->has('kelas')) {
                $query->where('kelas', $request->kelas);
            }

            // Filter by mata_pelajaran
            if ($request->has('mata_pelajaran')) {
                $query->where('mata_pelajaran', $request->mata_pelajaran);
            }

            // Filter by tipe
            if ($request->has('tipe')) {
                $query->where('tipe', $request->tipe);
            }

            // Filter for specific guru or kurikulum
            if (in_array($request->user()->role, ['guru', 'kurikulum'])) {
                $query->where('guru_id', $request->user()->id);
            }

            $assignments = $query->orderBy('deadline', 'desc')->get();

            // Tambahkan informasi submission untuk siswa
            if ($request->user()->role === 'siswa') {
                $assignments->each(function ($assignment) use ($request) {
                    $submission = AssignmentSubmission::where('assignment_id', $assignment->id)
                        ->where('siswa_id', $request->user()->id)
                        ->first();

                    $assignment->is_submitted = $submission ? true : false;
                    $assignment->submission_status = $submission ? $submission->status : null;
                    $assignment->submitted_at = $submission ? $submission->submitted_at : null;
                });
            }

            return response()->json([
                'success' => true,
                'message' => 'Assignments retrieved successfully',
                'data' => $assignments
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to retrieve assignments',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get assignment detail
     */
    public function show($id, Request $request)
    {
        try {
            $assignment = Assignment::with(['guru:id,name,email'])->findOrFail($id);

            // Jika siswa, tambahkan info submission
            if ($request->user()->role === 'siswa') {
                $submission = AssignmentSubmission::where('assignment_id', $id)
                    ->where('siswa_id', $request->user()->id)
                    ->first();

                $assignment->submission = $submission;
            }

            // Jika guru atau kurikulum, tambahkan statistik submission
            if (in_array($request->user()->role, ['guru', 'kurikulum'])) {
                $assignment->total_submissions = AssignmentSubmission::where('assignment_id', $id)->count();
                $assignment->graded_count = AssignmentSubmission::where('assignment_id', $id)
                    ->where('status', 'graded')->count();
            }

            return response()->json([
                'success' => true,
                'message' => 'Assignment detail retrieved successfully',
                'data' => $assignment
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Assignment not found',
                'error' => $e->getMessage()
            ], Response::HTTP_NOT_FOUND);
        }
    }

    /**
     * Create new assignment (Guru only)
     */
    public function store(Request $request)
    {
        try {
            $validator = Validator::make($request->all(), [
                'kelas' => 'required|string|max:10',
                'mata_pelajaran' => 'required|string|max:255',
                'judul' => 'required|string|max:255',
                'deskripsi' => 'required|string',
                'deadline' => 'required|date|after:now',
                'tipe' => 'required|in:tugas,ulangan,ujian',
                'bobot' => 'required|integer|min:1|max:100',
                'file' => 'nullable|file|mimes:pdf,doc,docx,ppt,pptx,jpg,jpeg,png|max:10240' // Max 10MB
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation failed',
                    'errors' => $validator->errors()
                ], Response::HTTP_UNPROCESSABLE_ENTITY);
            }

            $data = $validator->validated();
            $data['guru_id'] = $request->user()->id;

            // Handle file upload
            if ($request->hasFile('file')) {
                $file = $request->file('file');
                $filename = time() . '_' . $file->getClientOriginalName();
                $filePath = $file->storeAs('assignments', $filename, 'public');
                $data['file_path'] = $filePath;
            }

            $assignment = Assignment::create($data);

            return response()->json([
                'success' => true,
                'message' => 'Assignment created successfully',
                'data' => $assignment->load('guru:id,name,email')
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to create assignment',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update assignment (Guru only)
     */
    public function update(Request $request, $id)
    {
        try {
            $assignment = Assignment::findOrFail($id);

            // Check if guru owns this assignment
            if ($assignment->guru_id !== $request->user()->id) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to update this assignment'
                ], Response::HTTP_FORBIDDEN);
            }

            $validator = Validator::make($request->all(), [
                'kelas' => 'sometimes|string|max:10',
                'mata_pelajaran' => 'sometimes|string|max:255',
                'judul' => 'sometimes|string|max:255',
                'deskripsi' => 'sometimes|string',
                'deadline' => 'sometimes|date|after:now',
                'tipe' => 'sometimes|in:tugas,ulangan,ujian',
                'bobot' => 'sometimes|integer|min:1|max:100',
                'file' => 'nullable|file|mimes:pdf,doc,docx,ppt,pptx,jpg,jpeg,png|max:10240'
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation failed',
                    'errors' => $validator->errors()
                ], Response::HTTP_UNPROCESSABLE_ENTITY);
            }

            $data = $validator->validated();

            // Handle file upload
            if ($request->hasFile('file')) {
                // Delete old file if exists
                if ($assignment->file_path) {
                    Storage::disk('public')->delete($assignment->file_path);
                }

                $file = $request->file('file');
                $filename = time() . '_' . $file->getClientOriginalName();
                $filePath = $file->storeAs('assignments', $filename, 'public');
                $data['file_path'] = $filePath;
            }

            $assignment->update($data);

            return response()->json([
                'success' => true,
                'message' => 'Assignment updated successfully',
                'data' => $assignment->load('guru:id,name,email')
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to update assignment',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete assignment (Guru only)
     */
    public function destroy($id, Request $request)
    {
        try {
            $assignment = Assignment::findOrFail($id);

            // Check if guru owns this assignment
            if ($assignment->guru_id !== $request->user()->id) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to delete this assignment'
                ], Response::HTTP_FORBIDDEN);
            }

            // Delete file if exists
            if ($assignment->file_path) {
                Storage::disk('public')->delete($assignment->file_path);
            }

            $assignment->delete();

            return response()->json([
                'success' => true,
                'message' => 'Assignment deleted successfully'
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to delete assignment',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Submit assignment (Siswa only)
     */
    public function submit(Request $request, $id)
    {
        try {
            $assignment = Assignment::findOrFail($id);

            // Check if already submitted
            $existingSubmission = AssignmentSubmission::where('assignment_id', $id)
                ->where('siswa_id', $request->user()->id)
                ->first();

            if ($existingSubmission) {
                return response()->json([
                    'success' => false,
                    'message' => 'You have already submitted this assignment'
                ], Response::HTTP_CONFLICT);
            }

            $validator = Validator::make($request->all(), [
                'keterangan' => 'nullable|string',
                'file' => 'required|file|mimes:pdf,doc,docx,jpg,jpeg,png,zip,rar|max:10240'
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation failed',
                    'errors' => $validator->errors()
                ], Response::HTTP_UNPROCESSABLE_ENTITY);
            }

            $data = [
                'assignment_id' => $id,
                'siswa_id' => $request->user()->id,
                'keterangan' => $request->keterangan,
                'submitted_at' => now(),
                'status' => now() > $assignment->deadline ? 'late' : 'pending'
            ];

            // Handle file upload
            if ($request->hasFile('file')) {
                $file = $request->file('file');
                $filename = time() . '_' . $request->user()->id . '_' . $file->getClientOriginalName();
                $filePath = $file->storeAs('submissions', $filename, 'public');
                $data['file_path'] = $filePath;
            }

            $submission = AssignmentSubmission::create($data);

            return response()->json([
                'success' => true,
                'message' => 'Assignment submitted successfully',
                'data' => $submission->load(['siswa:id,name,email', 'assignment'])
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to submit assignment',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all submissions for an assignment (Guru only)
     */
    public function getSubmissions($id, Request $request)
    {
        try {
            $assignment = Assignment::findOrFail($id);

            // Check if guru owns this assignment
            if ($assignment->guru_id !== $request->user()->id && $request->user()->role !== 'admin' && $request->user()->role !== 'kepala_sekolah') {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to view submissions'
                ], Response::HTTP_FORBIDDEN);
            }

            $submissions = AssignmentSubmission::where('assignment_id', $id)
                ->with(['siswa:id,name,email', 'grade'])
                ->orderBy('submitted_at', 'asc')
                ->get();

            return response()->json([
                'success' => true,
                'message' => 'Submissions retrieved successfully',
                'data' => $submissions
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to retrieve submissions',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}

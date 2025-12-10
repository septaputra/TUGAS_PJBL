<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class RoleMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     * @param  string  $roles
     */
    public function handle(Request $request, Closure $next, string $roles): Response
    {
        if (!$request->user()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized'
            ], 401);
        }

        $allowedRoles = explode(',', $roles);
        $userRole = strtolower($request->user()->role); // Convert to lowercase for comparison

        // Convert all allowed roles to lowercase for case-insensitive comparison
        $allowedRoles = array_map('strtolower', $allowedRoles);

        if (!in_array($userRole, $allowedRoles)) {
            return response()->json([
                'success' => false,
                'message' => 'Forbidden. Insufficient role permissions.',
                'user_role' => $request->user()->role, // Debug info
                'required_roles' => $roles // Debug info
            ], 403);
        }

        return $next($request);
    }
}

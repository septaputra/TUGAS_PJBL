<?php

namespace App\Filament\Widgets;

use Filament\Widgets\Widget;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

// Models used dynamically
use App\Models\Subject;
use App\Models\Teacher;
use App\Models\User;
use App\Models\Schedule;
use App\Models\Kelas;
use App\Models\SubstituteTeacher;

class SummaryCards extends Widget
{
    public string $view = 'filament.widgets.summary-cards';

    public array $cards = [];

    public function mount()
    {
        // Attempt to determine the current resource from the URL and compute real counts
        $slug = null;
        try {
            $path = request()->path();
            $parts = explode('/', $path);
            $idx = array_search('resources', $parts);
            if ($idx !== false && isset($parts[$idx + 1])) {
                $slug = $parts[$idx + 1];
            } else {
                $slug = end($parts);
            }
        } catch (\Throwable $e) {
            $slug = null;
        }

        $map = [
            'subjects' => Subject::class,
            'teachers' => Teacher::class,
            'users' => User::class,
            'schedules' => Schedule::class,
            'kelas' => Kelas::class,
            'substitute-teachers' => SubstituteTeacher::class,
            'substituteteachers' => SubstituteTeacher::class,
        ];

        $total = $active = $completed = $today = 0;

        if ($slug && isset($map[$slug]) && class_exists($map[$slug])) {
            $modelClass = $map[$slug];
            try {
                $total = $modelClass::count();

                // 'active'/'status' heuristics
                $table = (new $modelClass())->getTable();
                if (Schema::hasColumn($table, 'status')) {
                    $active = $modelClass::where('status', 'active')->count();
                    $completed = $modelClass::where('status', 'completed')->count();
                } elseif (Schema::hasColumn($table, 'is_active')) {
                    $active = $modelClass::where('is_active', 1)->count();
                }

                if (Schema::hasColumn($table, 'created_at')) {
                    $today = $modelClass::whereDate('created_at', now()->toDateString())->count();
                }
            } catch (\Throwable $e) {
                // fallback to zeros on error
            }
        }

        $this->cards = [
            ['label' => 'Total', 'value' => $total, 'color' => 'blue'],
            ['label' => 'Aktif', 'value' => $active, 'color' => 'green'],
            ['label' => 'Selesai', 'value' => $completed, 'color' => 'slate'],
            ['label' => 'Hari Ini', 'value' => $today, 'color' => 'purple'],
        ];
    }

    public function render(): \Illuminate\Contracts\View\View
    {
        return view($this->view, [
            'cards' => $this->cards,
        ]);
    }
}

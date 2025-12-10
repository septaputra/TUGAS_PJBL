<?php

namespace App\Filament\Widgets;

use Filament\Widgets\Widget;

class ManagementHeader extends Widget
{
    public string $view = 'filament.widgets.management-header';

    public ?string $title = null;
    public ?string $subtitle = null;

    // Computed create URL (resource create page)
    public string $createUrl = '#';

    public function mount(string $title = null, string $subtitle = null)
    {
        $this->title = $title;
        $this->subtitle = $subtitle;

        try {
            $current = request()->url();
            $this->createUrl = rtrim($current, '/') . '/create';
        } catch (\Throwable $e) {
            $this->createUrl = '#';
        }
    }

    public function render(): \Illuminate\Contracts\View\View
    {
        return view($this->view, [
            'title' => $this->title,
            'subtitle' => $this->subtitle,
            'createUrl' => $this->createUrl,
        ]);
    }
}

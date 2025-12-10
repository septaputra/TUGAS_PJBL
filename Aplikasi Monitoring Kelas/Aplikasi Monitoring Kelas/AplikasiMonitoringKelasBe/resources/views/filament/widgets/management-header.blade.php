<link rel="stylesheet" href="{{ asset('css/filament-widgets.css') }}">

<div class="fmw-header">
    <div class="fmw-title">
        <h1>{{ $title ?? 'Manajemen' }}</h1>
        @if($subtitle)
            <p class="fmw-subtitle">{{ $subtitle }}</p>
        @endif
    </div>

    <div class="fmw-actions">
        <a href="#" class="fmw-btn fmw-btn-success" onclick="(function(e){e.preventDefault();var btn=document.querySelector('[data-action=\"export\"]')||Array.from(document.querySelectorAll('button,a')).find(x=>/export/i.test(x.innerText));if(btn){btn.click();return;}window.location.href=window.location.href.split('?')[0]+'?export=1';})(event);">
            <span>Export Excel</span>
        </a>

        <a href="#" class="fmw-btn fmw-btn-warning" onclick="(function(e){e.preventDefault();var btn=document.querySelector('[data-action=\"import\"]')||Array.from(document.querySelectorAll('button,a')).find(x=>/import/i.test(x.innerText));if(btn){btn.click();return;}alert('Import action not found on this page. Use header import if available.');})(event);">
            <span>Import Excel</span>
        </a>

        <a href="{{ $createUrl ?? '#' }}" class="fmw-btn fmw-btn-primary">
            <span>+ Tambah</span>
        </a>
    </div>
</div>

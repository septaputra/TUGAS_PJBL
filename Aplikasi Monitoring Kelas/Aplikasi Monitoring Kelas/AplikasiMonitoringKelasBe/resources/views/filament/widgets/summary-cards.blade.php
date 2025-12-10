<link rel="stylesheet" href="{{ asset('css/filament-widgets.css') }}">

<div class="fmw-cards">
    @foreach($cards as $card)
        <div class="fmw-card">
            <div>
                <div class="fmw-card-label">{{ $card['label'] }}</div>
                <div class="fmw-card-value">{{ $card['value'] }}</div>
            </div>
            <div class="fmw-card-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="10" stroke="#c7c7c7" stroke-width="2"/></svg>
            </div>
        </div>
    @endforeach
</div>

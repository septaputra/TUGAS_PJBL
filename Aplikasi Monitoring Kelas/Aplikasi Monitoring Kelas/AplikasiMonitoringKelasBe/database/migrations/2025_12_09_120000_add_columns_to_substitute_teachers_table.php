<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('substitute_teachers', function (Blueprint $table) {
            $table->string('name')->after('id');
            $table->string('specialization')->nullable()->after('name');
            $table->string('phone_number')->nullable()->after('specialization');
            $table->date('available_from')->nullable()->after('phone_number');
            $table->date('available_until')->nullable()->after('available_from');
            $table->text('notes')->nullable()->after('available_until');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('substitute_teachers', function (Blueprint $table) {
            $table->dropColumn([
                'name',
                'specialization',
                'phone_number',
                'available_from',
                'available_until',
                'notes',
            ]);
        });
    }
};

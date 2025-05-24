package com.behnamuix.tenserpingx.src.ClassExample.MafiSim

enum class Role {
    MAFIA,          // عضو مافیا
    SHAHR,        // شهروند معمولی
    DOCTOR,         // دکتر (می‌تواند هر شب یک نفر را درمان کند)
    KARAGAH,      // کارآگاه (می‌تواند هر شب هویت یک نفر را بررسی کند)
    GODFATHER,      // رئیس مافیا (مقاوم به تشخیص کارآگاه)
    SNIPER,         // تیرانداز (می‌تواند یک بار در بازی شلیک کند)
    ZEREHPOSH,        // زره‌پوش (مقاوم در برابر حمله مافیا در شب اول)
    LOVER           // عاشق (اگر کشته شود، معشوقه هم می‌میرد)
}
package com.behnamuix.tenserpingx.src.ClassExample.MafiSim

enum class Roles {
    MAFIA,          // عضو مافیا
    CITIZEN,        // شهروند معمولی
    DOCTOR,         // دکتر (می‌تواند هر شب یک نفر را درمان کند)
    DETECTIVE,      // کارآگاه (می‌تواند هر شب هویت یک نفر را بررسی کند)
    GODFATHER,      // رئیس مافیا (مقاوم به تشخیص کارآگاه)
    SNIPER,         // تیرانداز (می‌تواند یک بار در بازی شلیک کند)
    ARMORED,        // زره‌پوش (مقاوم در برابر حمله مافیا در شب اول)
    LOVER           // عاشق (اگر کشته شود، معشوقه هم می‌میرد)
}
// lib/core/theme/app_theme.dart
import 'package:flutter/material.dart';

enum AppThemeMode { light, dark, barberGold }

class AppTheme {
  // 1. Cấu hình màu cho LIGHT MODE (Nền sáng)
  static ThemeData get lightTheme {
    return ThemeData(
      brightness: Brightness.light,
      primaryColor: const Color(0xFF2A2A2A), // Màu chính (Đen xám)
      scaffoldBackgroundColor: const Color(0xFFF8F9FA), // Màu nền Screen chuẩn bài
      appBarTheme: const AppBarTheme(
        backgroundColor: Colors.white,
        elevation: 0,
        iconTheme: IconThemeData(color: Colors.black),
        titleTextStyle: TextStyle(color: Colors.black, fontSize: 18, fontWeight: FontWeight.bold),
      ),
      textTheme: const TextTheme(
        bodyLarge: TextStyle(color: Colors.black),
        bodyMedium: TextStyle(color: Colors.black87),
      ),
    );
  }

  // 2. Cấu hình màu cho DARK MODE (Nền tối)
  static ThemeData get darkTheme {
    return ThemeData(
      brightness: Brightness.dark,
      primaryColor: Colors.amber, 
      scaffoldBackgroundColor: const Color(0xFF121212), // Nền tối chuẩn AMOLED/OLED
      appBarTheme: const AppBarTheme(
        backgroundColor: Color(0xFF1E1E1E),
        elevation: 0,
        iconTheme: IconThemeData(color: Colors.white),
        titleTextStyle: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
      ),
      textTheme: const TextTheme(
        bodyLarge: TextStyle(color: Colors.white),
        bodyMedium: TextStyle(color: Colors.white70),
      ),
    );
  }

  // 3. Cấu hình màu riêng cho Barber (Nền vàng Gold/Nâu tối sang trọng)
  static ThemeData get barberGoldTheme {
    return ThemeData(
      brightness: Brightness.dark,
      primaryColor: const Color(0xFFD4AF37), // Màu Vàng Gold đặc trưng tiệm tóc
      scaffoldBackgroundColor: const Color(0xFF1A1612), // Nền nâu đen hoàng gia
      appBarTheme: const AppBarTheme(
        backgroundColor: Color(0xFF28211A),
        elevation: 0,
        iconTheme: IconThemeData(color: Color(0xFFD4AF37)),
        titleTextStyle: TextStyle(color: Color(0xFFD4AF37), fontSize: 18, fontWeight: FontWeight.bold),
      ),
      textTheme: const TextTheme(
        bodyLarge: TextStyle(color: Color(0xFFFFF4E0)),
        bodyMedium: TextStyle(color: Color(0xFFE6D5BC)),
      ),
    );
  }
}
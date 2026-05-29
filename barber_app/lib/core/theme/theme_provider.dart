// lib/core/theme/theme_provider.dart
import 'package:flutter/material.dart';

import 'app_theme.dart';

class ThemeProvider extends ChangeNotifier {
  // Mặc định ban đầu mở app là Light Mode
  AppThemeMode _currentThemeMode = AppThemeMode.light;

  AppThemeMode get currentThemeMode => _currentThemeMode;

  // Trả về ThemeData tương ứng để nạp vào MaterialApp
  ThemeData get currentThemeData {
    switch (_currentThemeMode) {
      case AppThemeMode.dark:
        return AppTheme.darkTheme;
      case AppThemeMode.barberGold:
        return AppTheme.barberGoldTheme;
      case AppThemeMode.light:
        return AppTheme.lightTheme;
    }
  }

  // Hàm kích hoạt đổi màu sắc từ bất kỳ Screen nào
  void changeTheme(AppThemeMode mode) {
    _currentThemeMode = mode;
    notifyListeners(); // Ra lệnh cho toàn bộ các Screen vẽ lại giao diện theo màu mới
  }
}
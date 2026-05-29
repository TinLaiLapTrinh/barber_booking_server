import 'package:barber_app/core/theme/app_theme.dart';
import 'package:barber_app/core/theme/theme_provider.dart';
import 'package:barber_app/core/utils/provider/user_provider.dart';
import 'package:barber_app/feature/auth/screens/login_screen.dart';
import 'package:barber_app/feature/auth/services/auth_service.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class CustomerProfileScreen extends StatelessWidget {
  const CustomerProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 🎯 Triệu hồi 2 kho chứa RAM toàn cục để bốc data
    final userProvider = context.watch<UserProvider>();
    final themeProvider = context.watch<ThemeProvider>();
    
    final user = userProvider.currentUser;
    final theme = Theme.of(context);

    // 🛡️ Kiểm tra xem trạng thái hiện tại là Thành viên đã Login hay Khách vãng lai
    final bool isLoggedIn = user != null;

    return Scaffold(
      appBar: AppBar(
        title: const Text("Hồ Sơ Của Bạn"),
        centerTitle: true,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // ==========================================
            // 👤 KHU VỰC 1: HIỂN THỊ THÔNG TIN USER PROVIDER (HỖ TRỢ FALLBACK)
            // ==========================================
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    CircleAvatar(
                      radius: 45,
                      backgroundColor: theme.colorScheme.primary.withAlpha(40),
                      // Nếu có avatar thì nạp, không thì để null để hiện Icon đóng thế
                      backgroundImage: (isLoggedIn && user.avatar != null) ? NetworkImage(user.avatar!) : null,
                      child: (!isLoggedIn || user.avatar == null)
                          ? Icon(Icons.person, size: 45, color: theme.colorScheme.primary)
                          : null,
                    ),
                    const SizedBox(height: 16),
                    // 🏷️ Hiển thị tên: Có tài khoản thì hiện Full Name, không thì hiện Khách vãng lai
                    Text(
                      isLoggedIn ? "${user.lastName} ${user.firstName}" : "Khách hàng vãng lai",
                      style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 6),
                    // 🏷️ Hiển thị Email
                    Text(
                      isLoggedIn ? (user.email ?? "Chưa cập nhật Email") : "Chưa cập nhật thông tin",
                      style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
                    ),
                    const SizedBox(height: 4),
                    // 🏷️ Hiển thị Số điện thoại
                    Text(
                      isLoggedIn ? (user.phoneNumber ?? "Chưa có số điện thoại") : "Đăng nhập để tích điểm đổi quà",
                      style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),

            // ==========================================
            // ⚙️ KHU VỰC 2: BỘ LỰA CHỌN THEME ĐA DẠNG (ENUM) - LUÔN HOẠT ĐỘNG
            // ==========================================
            Text(
              "Giao diện & Cài đặt",
              style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 8.0),
                child: RadioGroup<AppThemeMode>(
                  groupValue: themeProvider.currentThemeMode,
                  onChanged: (AppThemeMode? value) {
                    if (value != null) themeProvider.changeTheme(value);
                  },
                  child: Column(
                    children: const [
                      RadioListTile<AppThemeMode>(
                        secondary: Icon(Icons.light_mode, color: Colors.orange),
                        title: Text("Chế độ nền sáng"),
                        value: AppThemeMode.light,
                      ),
                      Divider(height: 1, indent: 50),
                      RadioListTile<AppThemeMode>(
                        secondary: Icon(Icons.dark_mode, color: Colors.blueGrey),
                        title: Text("Chế độ nền tối"),
                        value: AppThemeMode.dark,
                      ),
                      Divider(height: 1, indent: 50),
                      RadioListTile<AppThemeMode>(
                        secondary: Icon(Icons.content_cut, color: Colors.amber),
                        title: Text("Giao diện Barber Gold"),
                        subtitle: Text("Tông màu vàng đen hoàng gia"),
                        value: AppThemeMode.barberGold,
                      ),
                    ],
                  ),
                ),
              ),
            ),
            const SizedBox(height: 40),

            // ==========================================
            // 🧹 KHU VỰC 3: NÚT ĐĂNG XUẤT / ĐĂNG NHẬP BIẾN HÌNH ĐỘNG
            // ==========================================
            ElevatedButton.icon(
              onPressed: () => _handleAuthAction(context, userProvider, isLoggedIn),
              icon: Icon(isLoggedIn ? Icons.logout : Icons.login, color: Colors.white),
              label: Text(
                isLoggedIn ? "ĐĂNG XUẤT" : "ĐĂNG NHẬP NGAY",
                style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white, fontSize: 16),
              ),
              style: ElevatedButton.styleFrom(
                
                backgroundColor: isLoggedIn ? Colors.redAccent : theme.colorScheme.primary,
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                elevation: 0,
              ),
            ),
          ],
        ),
      ),
    );
  }

  // 🧼 Hàm xử lý hành động: Tự biến đổi luồng tùy theo trạng thái Login
  Future<void> _handleAuthAction(BuildContext context, UserProvider userProvider, bool isLoggedIn) async {
    if (isLoggedIn) {
      
      final AuthService authService = AuthService();
      await authService.logout();
      userProvider.clearUser();
    }

    if (context.mounted) {
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (_) => const LoginScreen()),
        (route) => false, 
      );
    }
  }
}
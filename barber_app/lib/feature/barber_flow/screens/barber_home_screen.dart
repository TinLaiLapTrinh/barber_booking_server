import 'package:barber_app/core/theme/app_theme.dart';
import 'package:barber_app/core/theme/theme_provider.dart';
import 'package:barber_app/core/utils/provider/user_provider.dart';
import 'package:barber_app/feature/auth/screens/login_screen.dart';
import 'package:barber_app/feature/auth/services/auth_service.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class BarberProfileScreen extends StatelessWidget {
  const BarberProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 🎯 Triệu hồi các kho chứa trạng thái để bốc dữ liệu
    final userProvider = context.watch<UserProvider>();
    final themeProvider = context.watch<ThemeProvider>();
    
    final user = userProvider.currentUser;
    final theme = Theme.of(context);

    // 🛡️ Kiểm tra xem đã đăng nhập tài khoản Barber chưa hay là khách xem thử
    final bool isLoggedIn = user != null;

    return Scaffold(
      appBar: AppBar(
        title: const Text("Hồ Sơ Barber"),
        centerTitle: true,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // ==========================================
            // 👤 KHU VỰC 1: THÔNG TIN CƠ BẢN (CÓ FALLBACK AN TOÀN)
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
                      backgroundImage: (isLoggedIn && user.avatar != null) ? NetworkImage(user.avatar!) : null,
                      child: (!isLoggedIn || user.avatar == null)
                          ? Icon(Icons.person, size: 45, color: theme.colorScheme.primary)
                          : null,
                    ),
                    const SizedBox(height: 16),
                    // Tên hiển thị động
                    Text(
                      isLoggedIn ? "${user.lastName} ${user.firstName}" : "Barber vãng lai",
                      style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 4),
                    // Thẻ tag định danh vai trò Barber cho chuyên nghiệp
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10,vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.orange[800],
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Text(
                        "💈 BARBER STAFF",
                        style: TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold),
                      ),
                    ),
                    const SizedBox(height: 10),
                    // Email
                    Text(
                      isLoggedIn ? (user.email ?? "Chưa cập nhật Email") : "Chưa xác thực danh tính thợ",
                      style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
                    ),
                    const SizedBox(height: 4),
                    // Số điện thoại
                    Text(
                      isLoggedIn ? (user.phoneNumber ?? "Chưa có số điện thoại") : "Vui lòng đăng nhập để nhận ca trực",
                      style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),

            // ==========================================
            // ⚙️ KHU VỰC 2: BỘ LỰA CHỌN THEME CHỈ CÓ TRẮNG ĐEN (LIGHT / DARK)
            // ==========================================
            Text(
              "Cấu hình giao diện làm việc",
              style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 8.0),
                child: RadioGroup<AppThemeMode>(
                  groupValue: themeProvider.currentThemeMode == AppThemeMode.barberGold 
                      ? AppThemeMode.light
                      : themeProvider.currentThemeMode,
                  onChanged: (AppThemeMode? value) {
                    if (value != null) themeProvider.changeTheme(value);
                  },
                  child: Column(
                    children: const [
                      // 🌟 Chỉ giữ lại lựa chọn 1: Trắng (Light Mode)
                      RadioListTile<AppThemeMode>(
                        secondary: Icon(Icons.light_mode, color: Colors.orange),
                        title: Text("Chế độ nền sáng (Trắng)"),
                        value: AppThemeMode.light,
                      ),
                      Divider(height: 1, indent: 50),
                      
                      // 🌟 Chỉ giữ lại lựa chọn 2: Đen (Dark Mode)
                      RadioListTile<AppThemeMode>(
                        secondary: Icon(Icons.dark_mode, color: Colors.blueGrey),
                        title: Text("Chế độ nền tối (Đen)"),
                        value: AppThemeMode.dark,
                      ),
                    ],
                  ),
                ),
              ),
            ),
            const SizedBox(height: 40),

            // ==========================================
            // 🧹 KHU VỰC 3: NÚT ĐĂNG XUẤT / ĐĂNG NHẬP BIẾN HÌNH
            // ==========================================
            ElevatedButton.icon(
              onPressed: () => _handleAuthAction(context, userProvider, isLoggedIn),
              icon: Icon(isLoggedIn ? Icons.logout : Icons.login, color: Colors.white),
              label: Text(
                isLoggedIn ? "ĐĂNG XUẤT TÀI KHOẢN" : "ĐĂNG NHẬP NGAY",
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

  // 🧼 Hàm xử lý hành động đăng nhập/đăng xuất rạch ròi
  Future<void> _handleAuthAction(BuildContext context, UserProvider userProvider, bool isLoggedIn) async {
    if (isLoggedIn) {
      final AuthService authService = AuthService();
      await authService.logout(); // Xóa Token ổ đĩa
      userProvider.clearUser();   // Xóa sạch RAM
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
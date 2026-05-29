import 'package:barber_app/core/storage/token_storage.dart';
import 'package:barber_app/core/utils/provider/user_provider.dart';
import 'package:barber_app/feature/auth/screens/register_screen.dart';
import 'package:barber_app/shared/screens/barber_main_navigation.dart';
import 'package:barber_app/shared/screens/customer_main_navigation.dart';
import 'package:barber_app/shared/widget/custom_button.dart';
import 'package:barber_app/shared/widget/custom_text_form_field.dart';
import 'package:flutter/material.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:provider/provider.dart';

import '../models/requests/login_request.dart';
import '../services/auth_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _usernameCtrl = TextEditingController();
  final _passwordCtrl = TextEditingController();
  final AuthService _authService = AuthService();
  bool _isLoading = false;

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final request = LoginRequest(
        username: _usernameCtrl.text.trim(),
        password: _passwordCtrl.text.trim(),
      );

      final token = await _authService.login(request);

      if (token != null) {
        await TokenStorage.saveToken(token);
        if (!mounted) return;
        await Provider.of<UserProvider>(context, listen: false).fetchProfile();

        Map<String, dynamic> decodedToken = JwtDecoder.decode(token);
        String userRole = decodedToken['role'] ?? 'CUSTOMER';

        if (!mounted) return;
        // Hiện SnackBar xanh báo thành công
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Đăng nhập thành công!'),
            backgroundColor: Colors.green,
            behavior: SnackBarBehavior.floating,
          ),
        );

        if (userRole == "BARBER") {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => const BarberMainNavigation()),
          );
        } else {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => const CustomerMainNavigation()),
          );
        }
      } else {
        if (!mounted) return;
        _showErrorSnackBar(context, 'Tài khoản hoặc mật khẩu không đúng!');
      }
    } catch (e) {

      if (!mounted) return;
      
      String cleanErrorMessage = e.toString().replaceAll("Exception: ", "");

      _showErrorSnackBar(context, cleanErrorMessage);
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  void _showErrorSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            const Icon(Icons.error_outline, color: Colors.white), // Icon cảnh báo sinh động
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                message,
                style: const TextStyle(fontWeight: FontWeight.w500),
              ),
            ),
          ],
        ),
        backgroundColor: Colors.redAccent, // Đổi sang màu đỏ cảnh báo rực rỡ
        behavior: SnackBarBehavior.floating, // Bo góc nổi hiện đại
        duration: const Duration(seconds: 3),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text("Đăng nhập")),
      body: SafeArea(
        // 🎯 CẢI TIẾN 3: Bọc GestureDetector để người dùng bấm ra vùng trống tự động ẩn bàn phím ảo
        child: GestureDetector(
          onTap: () => FocusScope.of(context).unfocus(),
          child: Center(
            // 🎯 CẢI TIẾN 4 (CHÍ MẠNG): Dùng SingleChildScrollView để tự động cuộn lên khi bàn phím xuất hiện, bít hoàn toàn lỗi tràn 6px!
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24.0),
              // Bọc Form để quản lý các ô nhập liệu bên trong
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Text(
                      "Đăng nhập",
                      style: theme.textTheme.headlineLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 40),

                    CustomTextFormField(
                      controller: _usernameCtrl,
                      labelText: "Tên đăng nhập",
                      prefixIcon: Icons.account_circle,
                    ),
                    const SizedBox(height: 16),

                    CustomTextFormField(
                      controller: _passwordCtrl,
                      labelText: "Mật khẩu",
                      prefixIcon: Icons.lock,
                      isPassword: true, // Tự động bật con mắt đóng mở xịn sò
                    ),
                    const SizedBox(height: 32),

                    CustomButton(
                      text: "ĐĂNG NHẬP",
                      isLoading: _isLoading,
                      onPressed: _handleLogin,
                    ),

                    const SizedBox(height: 16),
                    TextButton(
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (_) => const RegisterScreen(),
                          ),
                        );
                      },
                      child: const Text("Chưa có tài khoản? Đăng ký ngay"),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

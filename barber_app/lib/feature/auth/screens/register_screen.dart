import 'dart:io';

import 'package:barber_app/feature/auth/models/requests/register_request.dart';
import 'package:barber_app/feature/auth/services/auth_service.dart';
import 'package:barber_app/shared/widget/custom_button.dart';
import 'package:barber_app/shared/widget/custom_text_form_field.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _firstNameCtrl = TextEditingController();
  final _lastNameCtrl = TextEditingController();
  final _usernameCtrl = TextEditingController();
  final _passwordCtrl = TextEditingController();
  final _passwordConfirmCtrl = TextEditingController();
  final _emailCtrl = TextEditingController();
  final _phoneNumberCtrl = TextEditingController();
  final AuthService _authService = AuthService();

  File? _avatarFile;
  final ImagePicker _picker = ImagePicker();
  bool _isLoading = false;
  bool _obscurePassword = true;
  // Hàm chọn ảnh từ Thư viện
  Future<void> _pickAvatar() async {
    final XFile? pickedFile = await _picker.pickImage(
      source: ImageSource.gallery,
      imageQuality: 80, // Nén nhẹ cho đỡ nặng mạng
    );

    if (pickedFile != null) {
      setState(() {
        _avatarFile = File(pickedFile.path);
      });
    }
  }

  void _handleRegister() async {
    if (!_formKey.currentState!.validate()) return;

    if (_passwordCtrl.text != _passwordConfirmCtrl.text) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Mật khẩu xác nhận không trùng khớp!")),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      final request = RegisterRequest(
        username: _usernameCtrl.text.trim(),
        password: _passwordCtrl.text.trim(),
        firstName: _firstNameCtrl.text.trim(),
        lastName: _lastNameCtrl.text.trim(),
        phone: _phoneNumberCtrl.text.trim(),
        email: _emailCtrl.text.trim(),
        imageFile: _avatarFile,
      );
      final isSuccess = await _authService.register(request: request);

      if (isSuccess) {
        if (!mounted) return;
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Đăng ký tài khoản thành công!")),
        );
        Navigator.pop(context); // Thành công thì lùi về màn hình Đăng nhập
      } else {
        if (!mounted) return;
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text("Đăng ký thất bại, vui lòng kiểm tra lại!"),
          ),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text("Lỗi kết nối: $e")));
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  void dispose() {
    _firstNameCtrl.dispose();
    _lastNameCtrl.dispose();
    _usernameCtrl.dispose();
    _passwordCtrl.dispose();
    _passwordConfirmCtrl.dispose();
    _emailCtrl.dispose();
    _phoneNumberCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Tạo Tài Khoản Mới")),
      body: GestureDetector(
        onTap: () => FocusScope.of(context).unfocus(),
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(20.0),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // --- DESIGN KHU VỰC CHỌN AVATAR HÌNH TRÒN XỊN SÒ ---
                Center(
                  child: Stack(
                    children: [
                      CircleAvatar(
                        radius: 60,
                        backgroundColor: Colors.grey[300],
                        backgroundImage: _avatarFile != null
                            ? FileImage(_avatarFile!)
                            : null,
                        child: _avatarFile == null
                            ? Icon(
                                Icons.person,
                                size: 60,
                                color: Colors.grey[600],
                              )
                            : null,
                      ),
                      Positioned(
                        bottom: 0,
                        right: 0,
                        child: CircleAvatar(
                          backgroundColor: Colors.brown,
                          radius: 20,
                          child: IconButton(
                            icon: const Icon(
                              Icons.camera_alt,
                              size: 18,
                              color: Colors.white,
                            ),
                            onPressed:
                                _pickAvatar, // Bấm vào icon camera để chọn ảnh
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 12),
                const Text(
                  "Chọn ảnh đại diện của bạn",
                  style: TextStyle(fontSize: 14, color: Colors.grey),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 24),

              CustomTextFormField(
                  controller: _lastNameCtrl,
                  labelText: "Họ và chữ lót",
                  prefixIcon: Icons.badge,
                ),
                const SizedBox(height: 16),

                // 2. Tên -> Hiện nút Next (Mặc định)
                CustomTextFormField(
                  controller: _firstNameCtrl,
                  labelText: "Tên",
                  prefixIcon: Icons.person_outline,
                ),
                const SizedBox(height: 16),

                // 3. Tên đăng nhập -> Hiện nút Next (Mặc định)
                CustomTextFormField(
                  controller: _usernameCtrl,
                  labelText: "Tên đăng nhập",
                  prefixIcon: Icons.account_circle,
                ),
                const SizedBox(height: 16),

                // 4. Địa chỉ Email -> Hiện nút Next (Mặc định)
                CustomTextFormField(
                  controller: _emailCtrl,
                  labelText: "Địa chỉ Email",
                  prefixIcon: Icons.email,
                  keyboardType: TextInputType.emailAddress,
                ),
                const SizedBox(height: 16),

                // 5. Số điện thoại -> Present Next button (Mặc định)
                CustomTextFormField(
                  controller: _phoneNumberCtrl,
                  labelText: "Số điện thoại",
                  prefixIcon: Icons.phone,
                  keyboardType: TextInputType.phone,
                ),
                const SizedBox(height: 16),

                // 6. Mật khẩu -> Hiện nút Next (Mặc định)
                CustomTextFormField(
                  controller: _passwordCtrl,
                  labelText: "Mật khẩu",
                  prefixIcon: Icons.lock,
                  isPassword: true,
                  externalObscureText: _obscurePassword,
                  externalSuffixIcon: IconButton(
                    icon: Icon(_obscurePassword ? Icons.visibility_off : Icons.visibility),
                    onPressed: () => setState(() => _obscurePassword = !_obscurePassword),
                  ),
                  customValidator: (val) => val!.length < 6
                      ? "Mật khẩu phải từ 6 ký tự trở lên"
                      : null,
                ),
                const SizedBox(height: 16),

                // 7. Ô ĐẶC BIỆT: Xác nhận mật khẩu (Ô cuối cùng của chuỗi)
                CustomTextFormField(
                  controller: _passwordConfirmCtrl,
                  labelText: "Xác nhận mật khẩu",
                  prefixIcon: Icons.lock_clock,
                  isPassword: true,
                  externalObscureText: _obscurePassword,
                  
                  // 🎯 CHIÊU FOCUS ĐỘC ĐÁO TẠI ĐÂY:
                  textInputAction: TextInputAction.done, // Đổi bàn phím thành nút Hoàn thành (Dấu tích xanh hoặc chữ Done)
                  onFieldSubmitted: (_) {
                    _handleRegister(); // Khách gõ xong bấm nút Done trên bàn phím là tự kích hoạt đăng ký luôn!
                  },
                ),
                const SizedBox(height: 32),

                CustomButton(
                  text: "ĐĂNG KÝ",
                  isLoading: _isLoading,
                  onPressed: _handleRegister,
                ),
                // ElevatedButton(
                //   onPressed: _isLoading ? null : _handleRegister,
                //   style: ElevatedButton.styleFrom(
                //     padding: const EdgeInsets.symmetric(vertical: 16),
                //     backgroundColor: Colors.brown,
                //     foregroundColor: Colors.white,
                //   ),
                //   child: _isLoading
                //       ? const CircularProgressIndicator(color: Colors.white)
                //       : const Text(
                //           "ĐĂNG KÝ NGAY",
                //           style: TextStyle(
                //             fontSize: 16,
                //             fontWeight: FontWeight.bold,
                //           ),
                //         ),
                // ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

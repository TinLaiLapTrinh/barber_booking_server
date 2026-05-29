import 'package:barber_app/feature/auth/models/responses/user_response.dart';
import 'package:barber_app/feature/auth/services/auth_service.dart';
import 'package:flutter/material.dart';


class UserProvider with ChangeNotifier {
  
  final AuthService _authService = AuthService();

  UserResponse? _currentUser;
  bool _isLoading = false;


  UserResponse? get currentUser => _currentUser;
  bool get isLoading => _isLoading;
  bool get hasData => _currentUser != null; 
  
  
  Future<void> fetchProfile() async {
    _isLoading = true;
    notifyListeners(); 

    try {
      // Gọi trực tiếp hàm profile() ngon lành ní đã viết trong AuthService
      final profileData = await _authService.profile();
      _currentUser = profileData; // Nạp dữ liệu vào két RAM thành công
    } catch (e) {
      _currentUser = null; // Lỡ lỗi sập mạng hoặc rớt phiên thì xóa trắng cho an toàn
      print("Lỗi tại UserProvider.fetchProfile: $e");
      rethrow; // Ném lỗi ra ngoài nếu màn hình UI muốn tóm lấy hiện SnackBar
    } finally {
      _isLoading = false;
      notifyListeners(); // Báo cho toàn app cập nhật dữ liệu người dùng mới!
    }
  }

  // 🧹 Hàm xóa trạng thái User trên RAM (Gọi khi người dùng bấm Đăng xuất)
  void clearUser() {
    _currentUser = null;
    notifyListeners(); // Xóa sạch bộ nhớ RAM và bắt UI re-render về trạng thái trống
  }
}
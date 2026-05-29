import 'dart:convert';

import 'package:barber_app/core/api/api_client.dart';
import 'package:barber_app/core/api/api_endpoints.dart';
import 'package:barber_app/core/storage/token_storage.dart';
import 'package:barber_app/feature/auth/models/requests/login_request.dart';
import 'package:barber_app/feature/auth/models/requests/register_request.dart';
import 'package:barber_app/feature/auth/models/responses/user_response.dart';

class AuthService {
  Future<String?> login(LoginRequest request) async {
    try {
      final response = await ApiClient.post(
        ApiEndpoints.login,
        request.toJson(),
        requiresAuth: false,
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        if (responseData['accessToken'] != null) {
          return responseData['accessToken'];
        }
      } else if (response.statusCode == 401 || response.statusCode == 403) {
        throw Exception('Sai tài khoản hoặc mật khẩu!');
      } else {
        throw Exception('Lỗi máy chủ: ${response.statusCode}');
      }
      return null;
    } catch (e) {
      throw Exception('Đăng nhập thất bại: $e');
    }
  }

  Future<void> logout() async {
    await TokenStorage.deleteToken();
  }

  Future<bool> register({required RegisterRequest request}) async {
    try {
      final Map<String, dynamic> combinedBody = request.toFormData();

      if (request.imageFile != null) {
        combinedBody['image'] = request.imageFile;
      }

      final response = await ApiClient.post(
        ApiEndpoints.registerCustomer,
        combinedBody,
        requiresAuth: false,
        isMultipart: true,
      );
      if (response.statusCode == 200 || response.statusCode == 201) {
        return true;
      }
      return false;
    } catch (e) {
      throw Exception('Đăng ký thất bại: $e');
    }
  }

  Future<UserResponse> profile() async {
    try {
      final response = await ApiClient.get(
        ApiEndpoints.getProfile,
        requiresAuth: true,
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        
        final UserResponse userProfile = UserResponse.fromJson(responseData);
        
        return userProfile;
      } else {
        
        throw Exception('Mã lỗi từ máy chủ: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Lấy thông tin người dùng thất bại: $e');
    }
  }
}

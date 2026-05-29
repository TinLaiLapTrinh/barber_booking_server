import 'dart:io';

class RegisterRequest {
  final String username;
  final String password;
  final String firstName;
  final String lastName;
  final String phone;
  final String email;
  final File? imageFile;

  RegisterRequest({
    required this.username,
    required this.password,
    required this.firstName,
    required this.lastName,
    required this.phone,
    required this.email,
    this.imageFile,
  });

  Map<String, String> toFormData() {
    return {
      "username": username,
      "password": password,
      "firstName": firstName,
      "lastName": lastName,
      "email": email,
      "phone": phone,
    };
  }
}
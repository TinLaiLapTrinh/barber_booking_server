class UserResponse {
  final int id;
  final String? firstName;
  final String? lastName;
  final String? email;
  final String? phoneNumber;
  final String? avatar;
  final String userType;
  final bool isActive;
  UserResponse({
    required this.id,
    this.firstName,
    this.lastName,
    this.email,
    this.phoneNumber,
    this.avatar,
    required this.userType,
    required this.isActive,
  });

  factory UserResponse.fromJson(Map<String, dynamic> json) {
    return UserResponse(
      id: json['id'] ?? 0,
      firstName: json['firstName'],
      lastName: json['lastName'],
      email: json['email'],
      phoneNumber: json['phoneNumber'] ?? json['phone'],
      avatar: json['avatar'],
      userType: json['userType'] ?? 'CUSTOMER',
      isActive: json['isActive'] ?? json['active'] ?? false,
    );
  }

  String get displayName {
    if (firstName == null && lastName == null) return "Người dùng";
    return "${lastName ?? ''} ${firstName ?? ''}".trim();
  }
}

import 'package:barber_app/core/storage/token_storage.dart';
import 'package:barber_app/core/theme/theme_provider.dart';
import 'package:barber_app/core/utils/provider/user_provider.dart';
import 'package:barber_app/feature/auth/screens/login_screen.dart';
import 'package:barber_app/shared/screens/barber_main_navigation.dart';
import 'package:barber_app/shared/screens/customer_main_navigation.dart';
import 'package:flutter/material.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:provider/provider.dart';

void main() async{
  WidgetsFlutterBinding.ensureInitialized();
  String? savedToken = await TokenStorage.getToken();
  Widget initialScreen = const LoginScreen();

  if (savedToken != null) {
    
    bool isTokenExpired = JwtDecoder.isExpired(savedToken);

    if (!isTokenExpired) {
      
      Map<String, dynamic> decodedToken = JwtDecoder.decode(savedToken);
      String userRole = decodedToken['role'] ?? 'CUSTOMER';

      if (userRole == "BARBER") {
        initialScreen = const BarberMainNavigation();
      } else {
        initialScreen = const CustomerMainNavigation();
      }
    } else {
      
      await TokenStorage.deleteToken();
    }
  }

 runApp(
    // 🎯 BỌC MULTIPROVIDER Ở ĐÂY ĐỂ NUÔI STATE TOÀN CỤC
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => UserProvider()),
        ChangeNotifierProvider(create: (_) => ThemeProvider()),
      ],
      // Dùng Builder để tạo ra một BuildContext mới nằm DƯỚI các Provider
      child: Builder(
        builder: (context) {
          // Lấy dữ liệu themeData tương ứng dựa theo cấu hình của ní
          final themeProvider = context.watch<ThemeProvider>();

          return MaterialApp(
            title: 'Barber App',
            debugShowCheckedModeBanner: false,
            // 🎯 NẠP THEME ĐỘC QUYỀN NÍ VIẾT VÀO ĐÂY:
            theme: themeProvider.currentThemeData, 
            home: initialScreen, // Giữ nguyên luồng nhảy màn hình thông minh của ní
          );
        },
      ),
    ),
  );
}



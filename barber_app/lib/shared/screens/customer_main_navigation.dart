import 'package:barber_app/feature/customer_flow/screens/customer_profile_screen.dart';
import 'package:flutter/material.dart';

class CustomerMainNavigation extends StatefulWidget {
  const CustomerMainNavigation({super.key});

  @override
  State<CustomerMainNavigation> createState() => _CustomerMainNavigationState();
}

class _CustomerMainNavigationState extends State<CustomerMainNavigation> {
  int _selectedIndex = 0;

  // Danh sách 4 màn hình của Khách
  final List<Widget> _screens = [
    const Center(child: Text("Màn hình Trang Chủ (Banner, Dịch vụ Hot)")),
    const Center(child: Text("Màn hình Cửa Hàng (Tìm kiếm, Đặt lịch)")),
    const Center(child: Text("Màn hình Lịch Hẹn Của Tôi (Sắp đến, Lịch sử)")),
    const CustomerProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold(
      body: _screens[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) => setState(() => _selectedIndex = index),
        type: BottomNavigationBarType.fixed, // Giữ cố định thanh nav
        selectedItemColor: theme.primaryColor, // Màu ăn theo Theme (Vàng Gold hoặc Đen)
        unselectedItemColor: theme.hintColor.withValues(alpha: 0.5),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home_outlined), activeIcon: Icon(Icons.home), label: 'Trang chủ'),
          BottomNavigationBarItem(icon: Icon(Icons.storefront_outlined), activeIcon: Icon(Icons.storefront), label: 'Cửa hàng'),
          BottomNavigationBarItem(icon: Icon(Icons.calendar_month_outlined), activeIcon: Icon(Icons.calendar_month), label: 'Lịch hẹn'),
          BottomNavigationBarItem(icon: Icon(Icons.person_outline), activeIcon: Icon(Icons.person), label: 'Tài khoản'),
        ],
      ),
    );
  }
}
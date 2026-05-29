import 'package:barber_app/feature/barber_flow/screens/barber_home_screen.dart';
import 'package:flutter/material.dart';

class BarberMainNavigation extends StatefulWidget {
  const BarberMainNavigation({super.key});

  @override
  State<BarberMainNavigation> createState() => _BarberMainNavigationState();
}

class _BarberMainNavigationState extends State<BarberMainNavigation> {
  int _selectedIndex = 0;

  // Danh sách 4 màn hình công việc của Thợ
  final List<Widget> _screens = [
    const Center(child: Text("Màn hình Lịch Làm Việc Hôm Nay (Timeline ca trực)")),
    const Center(child: Text("Màn hình Đơn Hàng Đảm Nhận (Cập nhật trạng thái)")),
    const Center(child: Text("Màn hình Thống Kê (Doanh thu, Số ca cắt, Đánh giá sao)")),
    const Center(child: BarberProfileScreen()),
  ];

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold(
      body: _screens[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) => setState(() => _selectedIndex = index),
        type: BottomNavigationBarType.fixed,
        selectedItemColor: theme.primaryColor,
        unselectedItemColor: theme.hintColor.withValues(alpha: 0.5),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.assignment_outlined), activeIcon: Icon(Icons.assignment), label: 'Lịch trình'),
          BottomNavigationBarItem(icon: Icon(Icons.content_paste_outlined), activeIcon: Icon(Icons.content_paste), label: 'Đơn hàng'),
          BottomNavigationBarItem(icon: Icon(Icons.bar_chart_outlined), activeIcon: Icon(Icons.bar_chart), label: 'Thống kê'),
          BottomNavigationBarItem(icon: Icon(Icons.settings_outlined), activeIcon: Icon(Icons.settings), label: 'Cài đặt'),
        ],
      ),
    );
  }
}
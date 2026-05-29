// lib/core/widgets/custom_search_bar.dart
import 'package:flutter/material.dart';

class CustomSearchBar extends StatelessWidget {
  final String hintText;
  final ValueChanged<String>? onChanged;
  final TextEditingController? controller;

  const CustomSearchBar({
    super.key,
    this.hintText = "Tìm kiếm cửa hàng, thợ cắt tóc...",
    this.onChanged,
    this.controller,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Container(
      decoration: BoxDecoration(
        color: theme.brightness == Brightness.light 
            ? Colors.white 
            : theme.cardColor,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.04),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: TextField(
        controller: controller,
        onChanged: onChanged,
        style: TextStyle(color: theme.textTheme.bodyLarge?.color),
        decoration: InputDecoration(
          hintText: hintText,
          hintStyle: TextStyle(color: theme.hintColor.withValues(alpha:0.7), fontSize: 14),
          prefixIcon: Icon(Icons.search, color: theme.hintColor),
          suffixIcon: controller != null && controller!.text.isNotEmpty
              ? IconButton(
                  icon: const Icon(Icons.clear, size: 18),
                  onPressed: () => controller!.clear(),
                )
              : null,
          border: InputBorder.none, // Xóa bỏ viền gạch ngang thô của mặc định
          contentPadding: const EdgeInsets.symmetric(vertical: 15, horizontal: 20),
        ),
      ),
    );
  }
}
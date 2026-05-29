// lib/core/widgets/custom_back_button.dart
import 'package:flutter/material.dart';

class CustomBackButton extends StatelessWidget {
  final VoidCallback? onTap;

  const CustomBackButton({super.key, this.onTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return GestureDetector(
      onTap: onTap ?? () => Navigator.pop(context),
      child: Container(
        margin: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          // Nền nút tự động tương phản nhẹ với nền màn hình
          color: theme.appBarTheme.backgroundColor?.withValues(alpha: 0.8) ?? Colors.black12,
          shape: BoxShape.circle,
          border: Border.all(color: theme.dividerColor..withValues(alpha: 0.1)),
        ),
        padding: const EdgeInsets.all(8),
        child: Icon(
          Icons.arrow_back_ios_new, // Icon mũi tên mỏng, đẹp sang chảnh
          size: 18,
          color: theme.iconTheme.color,
        ),
      ),
    );
  }
}
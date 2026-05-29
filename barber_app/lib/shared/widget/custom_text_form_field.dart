import 'package:flutter/material.dart';

class CustomTextFormField extends StatefulWidget {
  final TextEditingController controller;
  final String labelText;
  final IconData prefixIcon;
  final bool isRequired;
  final bool isPassword;
  final TextInputType keyboardType;
  final String? Function(String?)? customValidator;
  final bool? externalObscureText;
  final Widget? externalSuffixIcon;

  // 🎯 TẬN DỤNG FOCUS: Thêm 2 thuộc tính để điều khiển hành động của bàn phím
  final TextInputAction? textInputAction; // Nút Next hoặc Done trên bàn phím
  final void Function(String)? onFieldSubmitted; // Hành động khi nhấn nút đó

  const CustomTextFormField({
    super.key,
    required this.controller,
    required this.labelText,
    required this.prefixIcon,
    this.isRequired = true,
    this.isPassword = false,
    this.keyboardType = TextInputType.text,
    this.customValidator,
    this.externalObscureText,
    this.externalSuffixIcon,
    this.textInputAction, // Nhận vào hành động bàn phím
    this.onFieldSubmitted, // Nhận vào hàm xử lý
  });

  @override
  State<CustomTextFormField> createState() => _CustomTextFormFieldState();
}

class _CustomTextFormFieldState extends State<CustomTextFormField> {
  late bool _obscureText;

  @override
  void initState() {
    super.initState();
    _obscureText = widget.isPassword;
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final finalObscureText = widget.externalObscureText ?? (_obscureText && widget.isPassword);

    return TextFormField(
      controller: widget.controller,
      obscureText: finalObscureText,
      keyboardType: widget.keyboardType,
      
      // 🎯 ÁP DỤNG TRÒ FOCUS:
      // Nếu không truyền gì, mặc định các ô sẽ hiện nút "Next".
      textInputAction: widget.textInputAction ?? TextInputAction.next,
      onFieldSubmitted: widget.onFieldSubmitted,

      decoration: InputDecoration(
        labelText: widget.isRequired ? "${widget.labelText} *" : widget.labelText,
        prefixIcon: Icon(widget.prefixIcon),
        border: const OutlineInputBorder(),
        suffixIcon: widget.externalSuffixIcon ?? (widget.isPassword
            ? IconButton(
                icon: Icon(
                  finalObscureText ? Icons.visibility_off : Icons.visibility,
                ),
                onPressed: () => setState(() => _obscureText = !_obscureText),
              )
            : null),
      ),
      validator: (val) {
        if (widget.isRequired && (val == null || val.trim().isEmpty)) {
          return "Vui lòng điền ${widget.labelText.toLowerCase()}";
        }
        if (widget.customValidator != null) {
          return widget.customValidator!(val);
        }
        return null;
      },
    );
  }
}
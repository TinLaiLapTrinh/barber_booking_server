import 'dart:convert';
import 'dart:io';

import 'package:http/http.dart' as http;

import '../storage/token_storage.dart';

class ApiClient {
  static const String baseUrl = "https://giving-goat-beloved.ngrok-free.app";
  static Future<Map<String, String>> _getHeaders({
    bool requiresAuth = true,
    bool isMultipart = false,
  }) async {
    Map<String, String> headers = {

      if (!isMultipart) 'Content-Type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'ngrok-skip-browser-warning': 'true', 
    };
    if (requiresAuth) {
      String? token = await TokenStorage.getToken();
      if (token != null) {
        headers['Authorization'] = 'Bearer $token';
      }
    }
    return headers;
  }

  static Future<http.Response> get(
    String endpoint, {
    bool requiresAuth = true,
  }) async {
    final url = Uri.parse('${baseUrl.trim()}${endpoint.trim()}');
    final headers = await _getHeaders(requiresAuth: requiresAuth);
    return await http.get(url, headers: headers);
  }

  static Future<http.Response> post(
    String endpoint,
    Map<String, dynamic> body, {
    bool requiresAuth = true,
    bool isMultipart = false,
  }) async {
    final url = Uri.parse('${baseUrl.trim()}${endpoint.trim()}');
    final headers = await _getHeaders(requiresAuth: requiresAuth);
    if (isMultipart) {
      var request = http.MultipartRequest('POST', url);
      request.headers.addAll(headers);

      body.forEach((key, value) async {
        if (value is File) {
          request.files.add(await http.MultipartFile.fromPath(key, value.path));
        } else if (value is List<File>) {
          for (var file in value) {
            request.files.add(
              await http.MultipartFile.fromPath(key, file.path),
            );
          }
        } else {
          request.fields[key] = value.toString();
        }
      });

      var streamedResponse = await request.send();
      return await http.Response.fromStream(streamedResponse);
    } else {
      return await http.post(url, headers: headers, body: jsonEncode(body));
    }
  }

  static Future<http.Response> patch(
    String endpoint,
    Map<String, dynamic> body, {
    bool requiresAuth = true,
    bool isMultipart = false,
  }) async {
    final url = Uri.parse('${baseUrl.trim()}${endpoint.trim()}');

    final headers = await _getHeaders(requiresAuth: requiresAuth);

    if (isMultipart) {

      var request = http.MultipartRequest('PATCH', url);
      request.headers.addAll(headers);

      for (var entry in body.entries) {
        final key = entry.key;
        final value = entry.value;

        if (value is File) {
          request.files.add(await http.MultipartFile.fromPath(key, value.path));
        } else if (value is List<File>) {
          for (var file in value) {
            request.files.add(
              await http.MultipartFile.fromPath(key, file.path),
            );
          }
        } else if (value != null) {
          request.fields[key] = value.toString();
        }
      }

      var streamedResponse = await request.send();
      return await http.Response.fromStream(streamedResponse);
    }

    return await http.patch(url, headers: headers, body: jsonEncode(body));
  }

  static Future<http.Response> delete(
    String endpoint, {
    bool requiresAuth = true,
  }) async {
    final url = Uri.parse('${baseUrl.trim()}${endpoint.trim()}');
    final headers = await _getHeaders(requiresAuth: requiresAuth);
    return await http.delete(url, headers: headers);
  }
}

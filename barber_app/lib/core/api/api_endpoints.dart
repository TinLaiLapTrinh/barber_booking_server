// lib/core/api/api_endpoints.dart

class ApiEndpoints {
  // ===========================================================================
  // 1. USER CONTROLLER - Quản lý tài khoản và đăng nhập
  // ===========================================================================
  static const String login = '/api/users/login';
  static const String registerCustomer = '/api/users/customer';
  static const String registerBarber = '/api/users/barber';
  static const String getAllUsers = '/api/users';
  static const String getProfile = '/api/users/profile';
  static const String getCustomers = '/api/users/customers';
  static const String getBarbers = '/api/users/barbers';
  
  // Các API có tham số động của User (Đã đổi sang kiểu int)
  static String updateUserStatus(int id) => '/api/users/user/$id/update-status';
  static String getBarberDetail(int id) => '/api/users/barber/$id';
  static String getBarberWeekSchedule(int id) => '/api/users/barber/$id/week-schedule';
  static String getBarberShops(int id) => '/api/users/barber/$id/shop';
  static String getBarberRates(int id) => '/api/users/barber/$id/rates';

  // ===========================================================================
  // 2. ORDER CONTROLLER - Quản lý đặt lịch
  // ===========================================================================
  static const String ordersByDate = '/api/orders/order'; // GET: Theo ngày
  static const String createOrder = '/api/orders/order';  // POST: Đặt lịch
  static const String getAllOrders = '/api/orders';
  static const String orderHistory = '/api/orders/history-order'; // Phân trang

  // Các API có tham số động của Order (Đã đổi sang kiểu int)
  static String rateOrder(int id) => '/api/orders/order/$id/rating';
  static String updateOrder(int id) => '/api/orders/order/$id/update';
  static String getOrderDetail(int id) => '/api/orders/order/$id';
  static String cancelOrder(int id) => '/api/orders/order/$id/cancel';

  // ===========================================================================
  // 3. SHOP CONTROLLER - Quản lý chi nhánh
  // ===========================================================================
  static const String registerShop = '/api/shops/shop';
  static const String getAllShops = '/api/shops';

  // Các API có tham số động của Shop (Đã đổi sang kiểu int)
  static String createShopVoucher(int shopId) => '/api/shops/shop/$shopId/voucher';
  static String assignServiceToShop(int shopId) => '/api/shops/shop/$shopId/service';
  static String getShopBarbers(int shopId) => '/api/shops/$shopId/barbers';
  static String getShopVouchers(int shopId) => '/api/shops/shop/$shopId/vouchers/conditions';
  static String getShopServices(int shopId) => '/api/shops/shop/$shopId/services';
  static String getShopRates(int shopId) => '/api/shops/shop/$shopId/rates';
  static String getShopDetail(int id) => '/api/shops/shop/$id';
  
  // API kết hợp cả shopId và serviceId kiểu int
  static String getServiceDetail(int shopId, int serviceId) => 
      '/api/shops/shop/$shopId/shop-service/$serviceId/service-detail';

  // ===========================================================================
  // 4. SERVICE CONTROLLER - Quản lý dịch vụ chung
  // ===========================================================================
  static const String createService = '/api/services/service';
  static const String getAllServices = '/api/services';

  // Các API có tham số động của Service (Đã đổi sang kiểu int)
  static String createServiceDetail(int serviceId) => '/api/services/service/$serviceId/detail';
  static String getServiceDetailsGroup(int serviceId) => '/api/services/service/$serviceId/details';
  static String uploadServiceImages(int detailId) => '/api/services/detail/$detailId/images';

  // ===========================================================================
  // 5. PAYMENT CONTROLLER - Cổng thanh toán (VNPay / MoMo)
  // ===========================================================================
  static const String momoCallback = '/api/payments/momo-callback';
  static const String vnpayCallback = '/api/payments/vnpay-callback';

  // Các API thanh toán theo Đan hàng (Đã đổi sang kiểu int)
  static String payWithVNPay(int orderId) => '/api/payments/vnpay/$orderId';
  static String payWithMomo(int orderId) => '/api/payments/momo/$orderId';
  static String checkPaymentStatus(int orderId) => '/api/payments/status/$orderId';

  // ===========================================================================
  // 6. LOCATION CONTROLLER - Địa giới hành chính
  // ===========================================================================
  static const String getProvinces = '/api/location/provinces';
  
  // Lấy phường xã theo mã tỉnh (Giữ nguyên chuỗi String vì mã tỉnh thường chứa chữ hoặc số dạng chuỗi như "01", "79")
  static String getWards(String provinceCode) => '/api/location/wards/$provinceCode';

  // ===========================================================================
  // 7. DASHBOARD CONTROLLER - Thống kê hệ thống (ADMIN)
  // ===========================================================================
  static const String adminStats = '/api/admin/dashboard/stats';
}
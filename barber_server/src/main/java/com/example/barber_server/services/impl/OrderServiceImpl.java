package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_request.OrderDetailRequest;
import com.example.barber_server.dto.dto_request.OrderRequest;
import com.example.barber_server.dto.dto_request.RateRequest;
import com.example.barber_server.dto.dto_response.*;
import com.example.barber_server.exception.BusinessException;
import com.example.barber_server.exception.ResourceNotFoundException;
import com.example.barber_server.models.*;
import com.example.barber_server.models.enums.OrderStatus;
import com.example.barber_server.models.enums.PaymentMethod;
import com.example.barber_server.models.enums.PaymentStatus;
import com.example.barber_server.repositories.*;
import com.example.barber_server.services.OrderService;
import com.example.barber_server.services.VoucherService;
import com.example.barber_server.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopServiceDetailRepository shopServiceDetailRepository;
    private final VoucherService  voucherService;
    private final VoucherRepository voucherRepository;
    private final RateRepository rateRepository;


    @Override
    public List<OrderResponse> getOrdersByShopAndDate(Integer shopId, LocalDate date) {
        List<Order> orders = orderRepository.findByShopIdAndOrderDate(shopId,date);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkBarberConflict(Integer barberId, LocalDate date, LocalTime start, LocalTime end) {
        return orderRepository.isBarberBusy(barberId,date, start,end);
    }

    @Transactional
    @Override
    public Integer createFullOrder(OrderRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingDateTime = LocalDateTime.of(request.getOrderDate(), request.getStartTime());
        if (bookingDateTime.isBefore(now)) {
            throw new BusinessException("Không thể đặt lịch cho thời gian đã qua. Vui lòng chọn khung giờ khác!");
        }
        if (bookingDateTime.isBefore(now.plusMinutes(15))) {
            throw new BusinessException("Vui lòng đặt lịch trước ít nhất 15 phút!");
        }
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
        User barber = userRepository.findById(request.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cửa hàng"));

        Voucher voucher = request.getVoucherId() != null ?
                voucherRepository.findVoucherById(request.getVoucherId()) : null;
        if(request.getPaymentMethod()==null)
            request.setPaymentMethod(PaymentMethod.CASH);

        Order order = new Order();
        order.setCustomer(customer);
        order.setBarber(barber);
        order.setShop(shop);
        order.setVoucher(voucher);
        order.setOrderDate(request.getOrderDate());
        order.setStartTime(request.getStartTime());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderDetails(new HashSet<>());

        float runningTotal = 0;
        int runningTotalDuration = 0;

        for (OrderDetailRequest dReq : request.getDetails()) {
            ShopServiceDetail ssd = shopServiceDetailRepository.findById(dReq.getShopServiceDetailId())
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setShopServiceDetail(ssd);
            detail.setOriginalPrice(ssd.getPrice());
            detail.setFinalPrice(ssd.getPrice());

            order.getOrderDetails().add(detail);

            runningTotal += ssd.getPrice();
            runningTotalDuration += ssd.getServiceDetail().getDuration();
        }
        if (voucher != null) {

            Double totalAmount = (double) runningTotal;

            Double discountAmount = voucherService.calculateActualDiscount(voucher, totalAmount);

            float finalPrice = (float) (totalAmount - discountAmount);
            if (finalPrice < 0) finalPrice = 0;

            order.setFinalPrice(finalPrice);
        } else {
            order.setFinalPrice(runningTotal);
        }

        LocalTime calculatedEndTime = request.getStartTime().plusMinutes(runningTotalDuration);
        if (this.checkBarberConflict(barber.getId(), request.getOrderDate(), request.getStartTime(), calculatedEndTime)) {
            throw new IllegalStateException("Thợ đã bận trong khung giờ từ " + request.getStartTime() + " đến " + calculatedEndTime);
        }

        order.setEndTime(calculatedEndTime);
        order.setTotalPrice(runningTotal);
        order.setTotalDuration(runningTotalDuration);


        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

    @Override
    public List<OrderResponse> findByBarberAndOrderDateOrderByStartTimeAsc(Integer barberId, LocalDate orderDate) {
        List<Order> barberOrders = orderRepository.findByBarberIdAndOrderDateOrderByStartTimeAsc(barberId,orderDate);
        return barberOrders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> findByCustomerAndOrderDateOrderByStartTimeAsc(Integer customerId, LocalDate orderDate) {
        List<Order> barberOrders = orderRepository.findByCustomerIdAndOrderDateOrderByStartTimeAsc(customerId,orderDate);
        return barberOrders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private OrderDetailResponse mapToOrderDetailResponse(OrderDetail detail) {
        var ssd = detail.getShopServiceDetail();
        var serviceDetail = ssd.getServiceDetail();


        List<ImageResponse> imageResponses = serviceDetail.getServiceDetailImages().stream()
                .map(img -> new ImageResponse(img.getId(), img.getImage()))
                .toList();

        return new OrderDetailResponse(
                detail.getId(),
                ssd.getShopService().getService().getName(),
                serviceDetail.getServiceType(),
                new HashSet<>(imageResponses),
                detail.getFinalPrice(),
                detail.getOriginalPrice()
        );
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderDetailResponse> detailResponses = order.getOrderDetails().stream()
                .map(this::mapToOrderDetailResponse)
                .toList();
        VoucherResponse voucherResponse = null;

        if (order.getVoucher() != null) {
            Voucher voucher = order.getVoucher();

            voucherResponse = new VoucherResponse(
                    voucher.getId(),
                    voucher.getName(),
                    voucher.getDiscount(),
                    voucher.getExpiry(),
                    voucher.getDateStart(),
                    voucher.getExpiryDate(),
                    voucher.getShop().getId(),
                    voucher.getIsActive(),
                    voucher.getDiscountType(),
                    voucher.getMinOrderValue(),
                    voucher.getMaxDiscountValue(),
                    null,
                    null
            );
        }

        return new OrderResponse(
                order.getId(),
                order.getShop().getId(),
                order.getShop().getName(),
                order.getShop().getAddress(),
                order.getShop().getAvatar(),
                order.getShop().getBackground(),
                order.getShop().getLongitude(),
                order.getShop().getLatitude(),
                voucherResponse,
                order.getBarber().getId(),
                order.getBarber().getFirstName() + " " + order.getBarber().getLastName(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getOrderDate(),
                order.getStartTime(),
                order.getEndTime(),
                detailResponses,
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getStatus() != null ? order.getStatus().getDisplayValue() : null,
                order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null,
                order.getPaymentStatus() != null ? order.getPaymentStatus().getDisplayValue() : null,
                order.getPaymentMethod() != null ? order.getPaymentMethod().getDisplayValue() : null,
                order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null,
                order.getTotalPrice(),
                order.getFinalPrice(),
                order.getTotalDuration()
        );
    }

    @Override
    public MessageResponse updateOrder(Integer orderId, Map<String, String> params) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng ID: " + orderId));
        if (params.containsKey("status")) {
            String statusStr = params.get("status").toUpperCase();
            try {
                OrderStatus status = OrderStatus.valueOf(statusStr);
                if (order.getStatus() == OrderStatus.COMPLETED) {
                    throw new BusinessException("Đơn hàng đã hoàn thành, không thể đổi trạng thái!");
                }
                else if (order.getStatus() == OrderStatus.CANCELLED) {
                    throw new BusinessException("Đơn hàng đã huỷ, không thể đổi trạng thái!");
                }

                order.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Trạng thái '" + statusStr + "' không hợp lệ!");
            }
        }
        if(params.containsKey("paymentMethod")){
            String paymentMethodStr = params.get("paymentMethod").toUpperCase();
            try{
                PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);
                order.setPaymentMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Phương thức thanh toán '" + paymentMethodStr + "' không hợp lệ!");
            }
        }
        if(params.containsKey("paymentStatus")){
            if (!SecurityUtils.isAdmin() && !SecurityUtils.isBarber()) {
                throw new AccessDeniedException("Khách hàng không có quyền cập nhật trạng thái thanh toán!");
            }
            String paymentStatusStr = params.get("paymentStatus").toUpperCase();
            try{
                PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentStatusStr);
                order.setPaymentStatus(paymentStatus);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Phương thức thanh toán '" + paymentStatusStr + "' không hợp lệ!");
            }
        }
        orderRepository.save(order);
        return new MessageResponse("Cập nhật đơn hàng thành công!", order.getId());
    }

    @Override
    public MessageResponse cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        SecurityUtils.checkAuthority(order.getCustomer().getId());

        if (!SecurityUtils.isAdmin() && !SecurityUtils.isBarber()) {
            LocalDateTime appointmentTime = LocalDateTime.of(order.getOrderDate(), order.getStartTime());

            if (LocalDateTime.now().isAfter(appointmentTime.minusMinutes(60))) {
                throw new BusinessException("Không thể hủy lịch trước giờ hẹn 60 phút. Vui lòng liên hệ hotline!");
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
        return new MessageResponse("Đã hủy đơn hàng thành công!", order.getId());

    }

    @Override
    public Float getFinalPrice(Order order) {

        if (order.getVoucher() == null) {
            return order.getTotalPrice();
        }

        Voucher voucher = order.getVoucher();
        Float originalPrice = order.getTotalPrice();
        float discountAmount = 0;

        if (originalPrice < voucher.getMinOrderValue()) {
            return originalPrice;
        }

        if (Boolean.TRUE.equals(voucher.getDiscountType())) {
            discountAmount = (float) (originalPrice * (voucher.getDiscount() / 100.0));

            if (voucher.getMaxDiscountValue() != null && voucher.getMaxDiscountValue() > 0) {
                discountAmount = Math.min(discountAmount, voucher.getMaxDiscountValue().floatValue());
            }
        } else {

            discountAmount = voucher.getDiscount().floatValue();
        }

        float finalPrice = originalPrice - discountAmount;
        return Math.max(finalPrice, 0);
    }

    @Override
    public Page<ListOrderResponse> orderHistory(Integer customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByCustomerId(customerId, pageable);

        // Chỉ cần gọi Method Reference: this::mapToListOrderResponse
        return orders.map(this::mapToListOrderResponse);
    }

    @Override
    @Transactional
    public MessageResponse rating(Integer orderId, User customer, RateRequest rateRequest) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn hàng ID: " + orderId));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            return new MessageResponse("Bạn không có quyền đánh giá đơn hàng số: " , orderId);
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            return new MessageResponse("Chỉ có thể đánh giá đơn hàng sau khi đã hoàn thành!", orderId);
        }

        if (!order.getRates().isEmpty()) {
            return new MessageResponse("Đơn hàng này đã được đánh giá trước đó.", orderId);
        }

        Rate rate = new Rate();
        rate.setRating(rateRequest.getRating());
        rate.setContent(rateRequest.getContent());
        rate.setOrder(order);
        rate.setCustomer(customer);

        rateRepository.save(rate);

        return new MessageResponse("Đánh giá đơn hàng thành công! Cảm ơn bạn.", orderId);
    }

    @Override
    public List<BarberWeekScheduleResponse> getBarberScheduleByWeek(Integer barberId, LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Order> orders = orderRepository.findByBarberIdAndOrderDateBetweenOrderByOrderDateAscStartTimeAsc(
                barberId, startOfWeek, endOfWeek);

        return orders.stream()
                .map(o -> BarberWeekScheduleResponse.builder()
                        .orderId(o.getId())
                        .orderDate(o.getOrderDate())
                        .startTime(o.getStartTime())
                        .totalDuration(o.getTotalDuration())
                        .status(o.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIdAndPaymentStatus(Integer id, PaymentStatus paymentStatus) {
        return orderRepository.existsByIdAndPaymentStatus(id, paymentStatus);
    }

    @Override
    public OrderResponse getOrderById(Integer orderId, User user) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Không tìm thấy đơn hàng ID: " + orderId));
        if(!order.getBarber().getId().equals(user.getId())&&!order.getCustomer().getId().equals(user.getId()))
            throw new BusinessException("Người dùng không có thẩm quyền truy cập hành động này");
        return convertToResponse(order);
    }

    @Override
    public Page<ListOrderResponse> getAllOrder(Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        Page<Order> orderPage = orderRepository.findAllByOrderDate(today, pageable);

        return orderPage.map(this::mapToListOrderResponse);
    }

    private ListOrderResponse mapToListOrderResponse(Order order) {
        String barberName = (order.getBarber() != null)
                ? order.getBarber().getFirstName() + " " + order.getBarber().getLastName()
                : "N/A";
        String customerName = (order.getCustomer() != null)
                ? order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()
                : "N/A";

        String summary = (order.getOrderDetails() != null)
                ? order.getOrderDetails().stream()
                  .map(detail -> detail.getShopServiceDetail().getServiceDetail().getServiceType())
                  .collect(Collectors.joining(", "))
                : "";

        return ListOrderResponse.builder()
                .id(order.getId())
                .shopName(order.getShop() != null ? order.getShop().getName() : "N/A")
                .shopAddress(order.getShop() != null ? order.getShop().getAddress() : "")
                .shopAvatar(order.getShop() != null ? order.getShop().getAvatar() : "")
                .orderDate(order.getOrderDate())
                .startTime(order.getStartTime())
                .barberId(order.getBarber() != null ? order.getBarber().getId() : 0)
                .barberName(barberName)
                .customerId(order.getCustomer().getId())
                .customerName(customerName)
                .status(order.getStatus() != null ? order.getStatus().name() : "")
                .statusName(order.getStatus() != null ? order.getStatus().getDisplayValue() : "")
                .finalPrice(order.getFinalPrice())
                .serviceSummary(summary)
                .TotalDuration(order.getTotalDuration()) // Ní nhớ check xem DTO là total hay Total nhé
                .build();
    }

}

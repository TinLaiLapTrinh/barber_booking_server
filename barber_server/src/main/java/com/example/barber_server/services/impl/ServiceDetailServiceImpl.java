package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.ImageResponse;
import com.example.barber_server.models.ServiceCategory;
import com.example.barber_server.models.ServiceDetail;
import com.example.barber_server.models.ServiceDetailImage;
import com.example.barber_server.repositories.ServiceCategoryRepository;
import com.example.barber_server.repositories.ServiceDetailImageRepository;
import com.example.barber_server.repositories.ServiceDetailRepository;
import com.example.barber_server.repositories.ServiceRepository;
import com.example.barber_server.services.ServiceDetailService;
import com.example.barber_server.services.UploadImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceDetailServiceImpl implements ServiceDetailService {


    private final ServiceDetailRepository serviceDetailRepository;
    private final ServiceDetailImageRepository serviceDetailImageRepository;
    private final ServiceRepository serviceRepository;
    private final UploadImageService uploadImageService;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    public List<ServiceDetail> findAllByServiceId(Integer serviceId) {
        return serviceDetailRepository.findServiceDetailByService_Id(serviceId);
    }

    @Override
    public List<ServiceDetail> findAllByCategoryId(Integer categoryId) {
        return serviceDetailRepository.findServiceDetailByCategory_Id(categoryId);
    }

    @Override
    @Transactional
    public ServiceDetail addServiceDetail(Integer serviceId, Integer categoryId, ServiceDetail serviceDetail) {

        com.example.barber_server.models.Service parentService = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Service ID: " + serviceId));

        ServiceCategory category = serviceCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Category ID: " + categoryId));

        serviceDetail.setService(parentService);
        serviceDetail.setCategory(category);

        return serviceDetailRepository.save(serviceDetail);
    }

    @Override
    @Transactional
    public List<ImageResponse> uploadServiceDetailImages(Integer serviceDetailId, List<MultipartFile> files) {

        ServiceDetail detail = serviceDetailRepository.findById(serviceDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết dịch vụ để thêm ảnh"));

        return files.stream().map(file -> {
            try {
                // 1. Upload lên Cloudinary
                String url = uploadImageService.uploadImage(file);

                // 2. Tạo và lưu Entity
                ServiceDetailImage imgEntity = new ServiceDetailImage();
                imgEntity.setServiceDetail(detail);
                imgEntity.setImage(url);
                ServiceDetailImage savedEntity = serviceDetailImageRepository.save(imgEntity);

                // 3. MAP SANG DTO (Đây là bước giải quyết lỗi ByteBuddy)
                ImageResponse response = new ImageResponse();
                response.setId(savedEntity.getId());
                response.setUrl(savedEntity.getImage());

                return response;
            } catch (Exception e) {
                // Ném lỗi để @Transactional thực hiện Rollback nếu cần
                throw new RuntimeException("Lỗi khi xử lý file " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }).toList();
    }
}
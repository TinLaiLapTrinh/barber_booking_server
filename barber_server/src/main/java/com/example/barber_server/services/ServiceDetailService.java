package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.ImageResponse;
import com.example.barber_server.dto.dto_response.ServiceDetailResponse;
import com.example.barber_server.models.ServiceDetail;
import com.example.barber_server.models.ServiceDetailImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceDetailService {
    List<ServiceDetailResponse> findAllByServiceIdAndCategoryId(Integer serviceId, Integer categoryId);

    List<ServiceDetail> findAllByCategoryId(Integer categoryId);

    ServiceDetail addServiceDetail(Integer serviceId, Integer categoryId, ServiceDetail serviceDetail);

    List<ImageResponse> uploadServiceDetailImages(Integer serviceDetailId, List<MultipartFile> files);
}
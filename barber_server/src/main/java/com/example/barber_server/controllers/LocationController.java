package com.example.barber_server.controllers;

import com.example.barber_server.models.Province;
import com.example.barber_server.models.Ward;
import com.example.barber_server.services.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
@Tag(name = "Location Controller")
public class LocationController {
    private final LocationService locationService;


    @Operation(
            summary = "Lấy danh sách tỉnh",
            description = "API trả về toàn bộ danh sách tỉnh trong hệ thống"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách tỉnh thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/provinces")
    public List<Province> getProvince() {
        return locationService.getProvince();
    }


    @Operation(
            summary = "Lấy danh sách phường theo tỉnh",
            description = "API trả về danh sách phường dựa theo provinceCode"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách phường thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tỉnh"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/wards/{provinceCode}")
    public List<Ward> getWardByProvinceId(
            @Parameter(
                    description = "Mã tỉnh (provinceCode)",
                    example = "79",
                    required = true
            )
            @PathVariable String provinceCode
    ) {
        return locationService.getWardByProvinceId(provinceCode);
    }
}


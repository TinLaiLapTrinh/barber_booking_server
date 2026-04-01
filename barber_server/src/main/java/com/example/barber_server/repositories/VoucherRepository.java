package com.example.barber_server.repositories;

import com.example.barber_server.models.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Integer>, JpaSpecificationExecutor<Voucher> {

    Voucher findVoucherById(Integer id);

    List<Voucher> findByShopIdAndIsActiveTrue(Integer shopId);


}

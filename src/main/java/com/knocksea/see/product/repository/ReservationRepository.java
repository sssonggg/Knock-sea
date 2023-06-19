package com.knocksea.see.product.repository;

import com.knocksea.see.product.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {



    boolean existsByProductProductTypeAndProductProductId(String productLabelType, Long productId);

    Optional<Reservation> findByProductProductId(Long productId);
}

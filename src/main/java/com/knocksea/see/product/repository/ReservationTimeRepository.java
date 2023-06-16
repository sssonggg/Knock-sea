package com.knocksea.see.product.repository;

import com.knocksea.see.edu.entity.Edu;
import com.knocksea.see.product.entity.ReservationTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {

    boolean deleteByEduEduId(Edu edu);
}

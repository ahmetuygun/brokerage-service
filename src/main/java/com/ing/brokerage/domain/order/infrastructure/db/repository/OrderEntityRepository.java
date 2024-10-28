package com.ing.brokerage.domain.order.infrastructure.db.repository;

import com.ing.brokerage.domain.order.infrastructure.db.entity.OrderEntity;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @Query("SELECT MAX(a.id) FROM OrderEntity a")
    Long findMaxId();


    Page<OrderEntity> findByCustomerIdAndStatusAndCreateDateBetween(
            Long customerId,
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

}
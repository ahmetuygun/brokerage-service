package com.ing.brokerage.domain.order.infrastructure;

import com.ing.brokerage.domain.order.domain.info.OrderId;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import com.ing.brokerage.domain.order.domain.model.Order;
import com.ing.brokerage.domain.order.infrastructure.db.entity.OrderEntity;
import com.ing.brokerage.domain.order.infrastructure.db.repository.OrderEntityRepository;
import com.ing.brokerage.domain.order.domain.repository.OrderRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderEntityRepository orderEntityRepository;

    public OrderRepositoryImpl(OrderEntityRepository orderEntityRepository) {
        this.orderEntityRepository = orderEntityRepository;
    }

    @Override
    public Long generateId() {
        Long maxId = orderEntityRepository.findMaxId();
        return (maxId != null) ? maxId + 1 : 1;
    }

    @Override
    public void save(Order order) {
        orderEntityRepository.save(toEntity(order));
    }


    @Override
    public Optional<Order> retrieveOrder(Long orderId) {
        return orderEntityRepository.findById(orderId)
                .map(this::toDomain);
    }


    @Override
    public Page<Order> listOrder(Long customerId, OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderEntityRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(item -> toDomain(item));
    }

    @Override
    public List<Order> fetchPendingOrders() {
       return orderEntityRepository.findByStatus(OrderStatus.PENDING).stream().map(item -> toDomain(item)).collect(Collectors.toList());
    }

    private OrderEntity toEntity(Order order) {

        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setId(order.getId().value());
        orderEntity.setCustomerId(order.customerId());
        orderEntity.setAssetName(order.assetName());
        orderEntity.setOrderSide(order.orderSide());
        orderEntity.setSize(order.size());
        orderEntity.setPrice(order.price());
        orderEntity.setStatus(order.status());
        orderEntity.setCreateDate(order.createDate());
        orderEntity.setVersion(order.version());
        return orderEntity;
    }

    private Order toDomain(OrderEntity orderEntity) {
        return new Order(
                new OrderId(orderEntity.getId()),
                orderEntity.getCustomerId(),
                orderEntity.getAssetName(),
                orderEntity.getOrderSide(),
                orderEntity.getSize(),
                orderEntity.getPrice(),
                orderEntity.getStatus(),
                orderEntity.getCreateDate(),
                orderEntity.getVersion()
        );
    }

}

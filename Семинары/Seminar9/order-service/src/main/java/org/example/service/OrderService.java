package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Order;
import org.example.model.OrderResponse;
import org.example.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClientService userClientService;

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<OrderResponse> findOrderWithUserById(Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    var user = userClientService.getUserById(order.getUserId().intValue());
                    return OrderResponse.fromOrder(order, user);
                });
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }
}

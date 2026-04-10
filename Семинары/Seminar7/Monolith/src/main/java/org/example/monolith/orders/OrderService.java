package org.example.monolith.orders;

import org.example.monolith.users.User;
import org.example.monolith.users.UserRepository;
import org.example.monolith.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserService userService;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setUserId(orderDetails.getUserId());
        order.setAmount(orderDetails.getAmount());
        order.setOrderDate(orderDetails.getOrderDate());
        order.setStatus(orderDetails.getStatus());
        return orderRepository.save(order);
    }
    
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> getOrdersByAmountRange(Double minAmount, Double maxAmount) {
        return orderRepository.findByAmountBetween(minAmount, maxAmount);
    }
    
    /**
     * Пример метода, который обновляет две таблицы в одной транзакции:
     * 1. Обновляет статус заказа
     * 2. Обновляет время последней активности пользователя
     * 
     * Если любая операция завершится неудачей, оба изменения будут отменены.
     */
    @Transactional
    public void updateOrderStatusAndUserActivity(Long orderId, String newStatus) {
        // Обновляем статус заказа
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден с id: " + orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
        
        // Обновляем время последней активности пользователя
        userService.updateUser(order.getUserId());
        
        // Обе операции происходят в одной транзакции
        // Если любая операция завершится неудачей, оба изменения будут отменены
    }
}

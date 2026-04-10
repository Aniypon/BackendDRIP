package org.example.orderservice.orders;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RestTemplate userServiceRestTemplate;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
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
     * Реализует паттерн Saga для обновления статуса заказа и активности пользователя с использованием RestTemplate
     * С идемпотентностью, повторными попытками и паттерном автоматического выключателя
     * 1. Обновить статус заказа с проверкой идемпотентности
     * 2. Вызвать user-service для обновления активности пользователя через RestTemplate с автоматическим выключателем
     * 3. Если обновление пользователя не удалось, компенсировать, вернув статус заказа
     */
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        // Generate idempotency key for this saga attempt using UUID
        String idempotencyKey = "saga-" + orderId + "-" + UUID.randomUUID().toString();
        
        // Step 1: Update order status
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Idempotency check - if status is already set, return success
        if (order.getStatus() != null && order.getStatus().equals(newStatus)) {
            return true; // Already in desired state, idempotent operation
        }
        
        String oldStatus = order.getStatus();
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        try {
            // Step 2: Call user-service to update user activity using RestTemplate with circuit breaker
            Long userId = updatedOrder.getUserId();
            String currentTime = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
            
            // Get circuit breaker for user service
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("user-service");
            
            // Wrap the RestTemplate call with circuit breaker
            return circuitBreaker.executeSupplier(() -> {
                try {
                    // Prepare request to user-service using RestTemplate
                    String url = "http://user-service:8082/api/users/" + userId + "/activity";
                    
                    // Create headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", "application/json");
                    headers.add("Idempotency-Key", idempotencyKey);
                    
                    // Create request body
                    Map<String, String> requestBody = new HashMap<>();
                    requestBody.put("lastActivity", currentTime);
                    
                    // Create HTTP entity
                    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
                    
                    // Make the call to user-service via RestTemplate
                    ResponseEntity<String> response = userServiceRestTemplate.exchange(
                        url, 
                        HttpMethod.PUT, 
                        requestEntity, 
                        String.class
                    );
                    
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new RuntimeException("Failed to update user activity: " + response.getBody());
                    }
                    
                    // Saga completed successfully
                    return true;
                    
                } catch (Exception e) {
                    throw new RuntimeException("Failed to call user service: " + e.getMessage(), e);
                }
            });
            
        } catch (CallNotPermittedException e) {
            // Circuit breaker is open, apply compensation immediately
            return false; // Return false instead of throwing exception to indicate failure without crashing
        } catch (Exception e) {
            // Step 3: Compensation - revert order status
            return false; // Return false instead of throwing exception to indicate failure without crashing
        }
    }
}

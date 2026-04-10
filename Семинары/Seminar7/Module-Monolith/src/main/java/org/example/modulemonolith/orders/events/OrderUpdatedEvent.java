package org.example.modulemonolith.orders.events;

import org.springframework.modulith.events.Externalized;

@Externalized("order.updated")
public record OrderUpdatedEvent(Long orderId, String newStatus, Long userId) {
}

package org.example.modulemonolith.users;

import org.example.modulemonolith.orders.events.OrderUpdatedEvent;
import org.springframework.modulith.ApplicationModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@ApplicationModule(type = ApplicationModule.Type.OPEN)
public class UserModuleApi {
    
    private final UserService userService;
    
    public UserModuleApi(UserService userService) {
        this.userService = userService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderUpdated(OrderUpdatedEvent event) {
        userService.updateUserActivity(event.userId());
    }
}

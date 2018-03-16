package nl.sogeti.reactivespring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketTransaction {
    private  String orderId;
    private String event;
    private int amount;
    private String customerName;
    private LocalDateTime transactionDate;
}

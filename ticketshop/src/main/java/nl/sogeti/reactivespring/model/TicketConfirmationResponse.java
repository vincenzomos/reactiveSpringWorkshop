package nl.sogeti.reactivespring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketConfirmationResponse {

    private String orderId;
    private boolean success;
    private String event;
}

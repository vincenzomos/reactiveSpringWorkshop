package nl.sogeti.reactivespring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketRequest {

    private String event;
    private int amount;
    private String customerName;

}

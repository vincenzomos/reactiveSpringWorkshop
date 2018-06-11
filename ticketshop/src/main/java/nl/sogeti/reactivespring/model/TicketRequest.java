package nl.sogeti.reactivespring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class TicketRequest {

    private final  String event;
    private final Integer amount;
    private final String customerName;

    public String getEvent() {
        return event;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    @JsonCreator
    public TicketRequest(@JsonProperty("event") String event, @JsonProperty("customerName") String customerName, @JsonProperty("amount") Integer amount) {
        this.event = event;
        this.amount = amount;
        this.customerName = customerName;
    }


}

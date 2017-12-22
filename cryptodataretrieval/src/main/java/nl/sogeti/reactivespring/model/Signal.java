package nl.sogeti.reactivespring.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Signal {

    private String description;
    private LocalDateTime timeStamp;
    private Direction direction;
}

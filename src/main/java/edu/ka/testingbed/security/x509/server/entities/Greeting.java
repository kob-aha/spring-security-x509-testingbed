package edu.ka.testingbed.security.x509.server.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Greeting {
    private String name;
    private String message;
}

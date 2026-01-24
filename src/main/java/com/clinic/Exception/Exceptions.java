package com.clinic.Exception;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Exceptions {

    private String message;
    private HttpStatus status;
    private HttpStatusCode status_code;
}

package com.clinic.LookupResponse;


import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStatus {

    private String status_code;
    private String status_description;
}

package com.clinic.LookupResponse;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseIdentifier {

    private UUID clinic_id;
    private String clinic_code;
    private String clinic_name;
    private String token;
}

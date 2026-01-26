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

    public  ResponseIdentifier(UUID clinic_id, String clinic_code, String clinic_name){
        this.clinic_id = clinic_id;
        this.clinic_code =clinic_code;
        this.clinic_name = clinic_name;
    }

}

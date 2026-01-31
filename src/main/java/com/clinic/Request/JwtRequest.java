package com.clinic.Request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {
    private String clinicEmail;
    private String clinicPassword;
}

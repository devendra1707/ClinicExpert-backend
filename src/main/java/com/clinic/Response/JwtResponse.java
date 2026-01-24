package com.clinic.Response;

import com.clinic.Model.Clinic;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private Clinic clinic;
    private String token;
}

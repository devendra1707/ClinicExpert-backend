package com.clinic.LookupResponse;


import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {

    private String clinic_email;
    private String clinic_contact;
    private String clinic_status;
    private String clinic_address;
    private String clinic_city;
    private String clinic_state;
    private String clinic_pin_code;
    private String clinic_time_zone;
    private String clinicOpeningTime;
    private String clinicClosingTime;
    private String clinicSubscriptionPlan;
    private String ClinicLogo;

}

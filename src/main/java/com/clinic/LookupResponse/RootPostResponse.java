package com.clinic.LookupResponse;


import com.clinic.Model.Clinic;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RootPostResponse {

    private ResponseIdentifier response_identifier;
    private ResponseDatas response_datas;
    private ResponseStatus response_status;

}

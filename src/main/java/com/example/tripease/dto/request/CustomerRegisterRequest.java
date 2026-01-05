package com.example.tripease.dto.request;

import com.example.tripease.Enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterRequest {
    private String name;
    private int age;
    private Gender gender;
    private String emailId;
    private String password;
}

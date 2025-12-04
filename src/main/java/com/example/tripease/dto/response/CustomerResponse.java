package com.example.tripease.dto.response;

import com.example.tripease.Enum.Gender;
import jakarta.persistence.Entity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerResponse {
    private String name;
    private int age;
    private String emailId;
}

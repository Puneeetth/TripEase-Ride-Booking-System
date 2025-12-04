package com.example.tripease.transformer;

import com.example.tripease.dto.request.CustomerRequest;
import com.example.tripease.dto.response.CustomerResponse;
import com.example.tripease.model.Customer;

public class CustomerTransformer {
    public static Customer customerRequestToCustomer(CustomerRequest customerRequest){
//        Customer customer = new Customer();
////        customer.setName(customerRequest.getName());
////        customer.setAge(customerRequest.getAge());
////        customer.setGender(customerRequest.getGender());
////        customer.setEmailId(customerRequest.getEmailId());
      return Customer.builder()
               .name(customerRequest.getName())
               .age(customerRequest.getAge())
               .emailId(customerRequest.getEmailId())
               .gender(customerRequest.getGender())
               .build();
    }
    public static CustomerResponse customerToCustomerResponse(Customer customer){
        return CustomerResponse.builder()
                .name(customer.getName())
                .emailId(customer.getEmailId())
                .age(customer.getAge())
                .build();
    }
}

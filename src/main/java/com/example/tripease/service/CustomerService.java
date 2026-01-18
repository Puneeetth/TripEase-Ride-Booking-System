package com.example.tripease.service;

import com.example.tripease.Enum.Gender;
import com.example.tripease.dto.request.CustomerRequest;
import com.example.tripease.dto.response.CustomerResponse;
import com.example.tripease.exception.CustomerNotFoundException;
import com.example.tripease.model.Customer;
import com.example.tripease.repository.CustomerRepository;
import com.example.tripease.transformer.CustomerTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    CustomerRepository customerRepository;

    public CustomerResponse addCustomer(CustomerRequest customerRequest) {

        // convert RequestDTO -> Entity
        Customer customer = CustomerTransformer.customerRequestToCustomer(customerRequest);

        // save the Entity to DB
        Customer Customer = customerRepository.save(customer);
        // saved entity -> Response DTO
        return CustomerTransformer.customerToCustomerResponse(customer);
    }

    public CustomerResponse getCustomer(int customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Invalid customer id");
        }
        Customer savedCustomer = optionalCustomer.get();
        // saved entity -> Response DTO
        return CustomerTransformer.customerToCustomerResponse(savedCustomer);
    }

    public List<CustomerResponse> getAllByGender(Gender gender) {
        List<Customer> customers = customerRepository.findByGender(gender);

        // entity -> response dto
        List<CustomerResponse> customerResponses = new ArrayList<>();

        for (Customer customer : customers) {
            customerResponses.add(CustomerTransformer.customerToCustomerResponse(customer));
        }
        return customerResponses;
    }

    public List<CustomerResponse> getAllByGenderAndAge(Gender gender, int age) {

        List<Customer> customers = customerRepository.findByGenderAndAge(gender, age);

        List<CustomerResponse> customerResponses = new ArrayList<>();

        for (Customer customer : customers) {
            customerResponses.add(CustomerTransformer.customerToCustomerResponse(customer));
        }
        return customerResponses;
    }

    public List<CustomerResponse> getAllByGenderAndAgeGreaterThan(String gender, int age) {
        List<Customer> customers = customerRepository.findByGenderAndAgeGreaterThan(gender, age);

        List<CustomerResponse> customerResponses = new ArrayList<>();

        for (Customer customer : customers) {
            customerResponses.add(CustomerTransformer.customerToCustomerResponse(customer));
        }
        return customerResponses;
    }
}

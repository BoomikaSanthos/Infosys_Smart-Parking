package com.smartpark.service;

import com.smartpark.model.Company;
import com.smartpark.repository.CompanyRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // ----------------------
    // Create Company
    // ----------------------
    public Company createCompany(@NonNull Company company) {
        return companyRepository.save(company);
    }

    // ----------------------
    // Get All Companies
    // ----------------------
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
}


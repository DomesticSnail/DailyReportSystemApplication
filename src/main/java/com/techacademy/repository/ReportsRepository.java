package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Reports;

public interface ReportsRepository extends JpaRepository<Reports, Long> {
    // Additional custom queries can be added here if needed
    List<Reports> findByReportDate(LocalDate reportDate);
    List<Reports> findByEmployee(Employee employee);
}
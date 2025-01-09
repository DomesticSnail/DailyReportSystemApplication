package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Reports;

public interface ReportsRepository extends JpaRepository<Reports, Long> {
    // Additional custom queries can be added here if needed
}

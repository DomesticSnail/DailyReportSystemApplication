package com.techacademy.service;

import com.techacademy.entity.Reports;
import com.techacademy.repository.ReportsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportsService {

    private final ReportsRepository reportsRepository;

    public ReportsService(ReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    /** Retrieves all reports */
    @Transactional(readOnly = true)
    public List<Reports> getAllReports() {
        return reportsRepository.findAll();
    }

    /** Retrieves a single report by ID */
    @Transactional(readOnly = true)
    public Optional<Reports> getReportById(Long id) {
        return reportsRepository.findById(id);
    }

    /** Prepares the report data and saves it */
    @Transactional
    public void save(Reports reports, UserDetail userDetail) {
        // Attach the logged-in employee to the report
        reports.setEmployee(userDetail.getEmployee());
        reports.setDeleteFlag(false);

        // Set created_at and updated_at before saving
        LocalDateTime now = LocalDateTime.now();
        reports.setCreatedAt(now);
        reports.setUpdatedAt(now);

        // Save the report
        reportsRepository.save(reports);
    }

    /** Saves or updates a report */
    @Transactional
    public Reports saveReport(Reports report) {
        return reportsRepository.save(report);
    }

    /** Deletes a report by ID */
    @Transactional
    public void deleteReportById(Long id) {
        reportsRepository.deleteById(id);
    }

    /** Marks a report as deleted (soft delete) */
    @Transactional
    public void softDeleteReport(Long id) {
        Optional<Reports> optionalReport = reportsRepository.findById(id);
        if (optionalReport.isPresent()) {
            Reports report = optionalReport.get();
            report.setDeleteFlag(true);
            reportsRepository.save(report);
        }
    }

}
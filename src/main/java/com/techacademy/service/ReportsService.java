package com.techacademy.service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Reports;
import com.techacademy.repository.ReportsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public Reports getReportById(Long id) {
        return reportsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + id));
    }

    /** Saves or updates a report */
    @Transactional
    public ErrorKinds save(Reports reports, UserDetail userDetail) {
        // Check for duplicate report date
        if (isDuplicateDate(reports.getReportDate())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        // Proceed with saving the report if no duplication
        reports.setEmployee(userDetail.getEmployee());
        reports.setDeleteFlag(false);

        LocalDateTime now = LocalDateTime.now();
        reports.setCreatedAt(now);
        reports.setUpdatedAt(now);

        reportsRepository.save(reports);
        return ErrorKinds.SUCCESS;
    }

    /** Checks if a report with the same date already exists */
    private boolean isDuplicateDate(LocalDate reportDate) {
        List<Reports> existingReports = reportsRepository.findByReportDate(reportDate);
        return !existingReports.isEmpty();
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
package com.techacademy.service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Reports;
import com.techacademy.repository.ReportsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    /** Prepares a report for update, including any additional data for the view */
    @Transactional(readOnly = true)
    public Reports prepareReportForUpdate(Long id) {
        return getReportById(id); // Fetch the report and return it.
    }

    /** Handles the update logic for a report */
    @Transactional
    public void updateReport(Long id, Reports updatedReport, UserDetail userDetail) {
        Reports existingReport = getReportById(id);
        existingReport.setTitle(updatedReport.getTitle());
        existingReport.setContent(updatedReport.getContent());
        existingReport.setReportDate(updatedReport.getReportDate());
        existingReport.setUpdatedAt(LocalDateTime.now());

        reportsRepository.save(existingReport);
    }

    /** Saves or updates a report */
    @Transactional
    public ErrorKinds save(Reports reports, UserDetail userDetail) {
        if (isDuplicateDate(reports.getReportDate())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

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
}
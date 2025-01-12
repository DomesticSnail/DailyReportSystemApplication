package com.techacademy.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Reports;
import com.techacademy.service.ReportsService;
import com.techacademy.service.UserDetail;


@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping
    public String showReports(Model model) {
        // Fetch all reports from the service
        model.addAttribute("reportList", reportsService.getAllReports());
        model.addAttribute("listSize", reportsService.getAllReports().size());
        return "reports/reports"; // Return the report list page
    }

    @GetMapping("/{id}/detail")
    public String showReportDetail(@PathVariable("id") Long id, Model model) {
        // Fetch the report by its ID
        Reports report = reportsService.getReportById(id);

        // Add the report to the model
        model.addAttribute("report", report);

        return "reports/reportsdetail"; // Render the report detail page
    }

    @GetMapping(value = "/add")
    public String create(@ModelAttribute("reports") Reports reports, Model model, Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String fullName = userDetail.getEmployee().getName();

        model.addAttribute("loggedInUser", fullName);
        model.addAttribute("reports", reports);

        return "reports/reportsnew";
    }

    @PostMapping("/add")
    public String add(@Validated Reports reports, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        String fullName = userDetail.getEmployee().getName();
        model.addAttribute("loggedInUser", fullName);

        // Check for validation errors
        if (res.hasErrors()) {
            return "reports/reportsnew"; // Return to the form if there are validation errors
        }

        // Call save method from service and handle duplicate date error
        ErrorKinds result = reportsService.save(reports, userDetail);
        if (result == ErrorKinds.DATECHECK_ERROR) {
            model.addAttribute("reportDateError", "既に登録されている日付です");
            return "reports/reportsnew"; // Return to the form with the error message
        }

        return "redirect:/reports"; // Redirect to the reports list page
    }

    @PostMapping("/{id}/delete")
    public String deleteReport(@PathVariable("id") Long id) {
        // Delete the report by its ID
        reportsService.deleteReportById(id);

        // Redirect back to the reports list page
        return "redirect:/reports";
    }

    @GetMapping("/{id}/update")
    public String updateReport(@PathVariable("id") Long id, Model model) {
        // Fetch the report by its ID
        Reports report = reportsService.getReportById(id);

        // Extract the employee's name associated with the report
        String employeeName = report.getEmployee().getName();

        // Add the report and employee's name to the model
        model.addAttribute("report", report);
        model.addAttribute("employeeName", employeeName); // Pass the employee's name to the model

        return "reports/reportsupdate"; // Render the update page
    }

    @PostMapping("/{id}/update")
    public String saveUpdatedReport(
            @PathVariable("id") Long id,
            @Validated @ModelAttribute Reports updatedReport,
            BindingResult res,
            @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        // Fetch the existing report
        Reports existingReport = reportsService.getReportById(id);

        // Check for validation errors
        if (res.hasErrors()) {
            return "reports/reportsupdate"; // Return to the form if there are validation errors
        }

        // Call save method from service and handle duplicate date error
        ErrorKinds result = reportsService.save(updatedReport, userDetail);
        if (result == ErrorKinds.DATECHECK_ERROR) {
            model.addAttribute("reportDateError", "既に登録されている日付です");
            return "reports/reportsupdate"; // Return to the form with the error message
        }

        // If no validation errors, update the existing report
        existingReport.setTitle(updatedReport.getTitle());
        existingReport.setContent(updatedReport.getContent());
        existingReport.setReportDate(updatedReport.getReportDate());
        existingReport.setUpdatedAt(LocalDateTime.now());

        // Call the service to save the updated report
        reportsService.save(existingReport, userDetail);

        return "redirect:/reports/" + id + "/detail"; // Redirect to the report detail page
}

}
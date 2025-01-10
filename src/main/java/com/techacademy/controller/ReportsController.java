package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    // Show all reports in reports.html
    @GetMapping
    public String showReports(Model model) {
        model.addAttribute("reportList", reportsService.getAllReports());
        model.addAttribute("listSize", reportsService.getAllReports().size());
        return "Employees/reports";  // Return the reports view
    }

    // Create a new report (show the form)
    @GetMapping(value = "/add")
    public String create(@ModelAttribute("reports") Reports reports, Model model, Authentication authentication) {
        // Get the logged-in user's details (UserDetail)
        UserDetail userDetail = (UserDetail) authentication.getPrincipal(); // This is the custom UserDetail object

        // Access the logged-in user's full name (or any other attribute) from the Employee object
        String fullName = userDetail.getEmployee().getName();  // Assuming 'getFullName()' is a method in Employee

        // Set the full name in the model
        model.addAttribute("loggedInUser", fullName); // Ensure this is correct
        model.addAttribute("reports", reports);

        return "employees/reportsnew";  // Show the report creation form
    }

    // Save the new report and redirect to show reports
    @PostMapping("/add")
    public String saveReport(@ModelAttribute("reports") Reports reports, BindingResult result) {
        if (result.hasErrors()) {
            return "errorPage";  // Handle errors if necessary
        }

        // Ensure the reportDate is set to start of the day if not already done
        if (reports.getReportDate() != null) {
            reports.setReportDate(reports.getReportDate().toLocalDate().atStartOfDay());
        }

        // Save the report data
        reportsService.saveReport(reports);

        // Redirect to show the list of reports
        return "redirect:/reports";  // After saving, show the updated list of reports
    }
}
package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Reports;
import com.techacademy.service.ReportsService;
import com.techacademy.service.UserDetail;

import java.time.LocalDateTime;

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
        // Check for validation errors
        if (res.hasErrors()) {
            return "reports/reportsnew"; // Return to the form if there are validation errors
        }

        try {
            // Attach the logged-in employee to the report
            reports.setEmployee(userDetail.getEmployee());
            reports.setDeleteFlag(false);

            // Set created_at and updated_at before saving
            LocalDateTime now = LocalDateTime.now();
            reports.setCreatedAt(now);
            reports.setUpdatedAt(now);

            // Save the report using the reportsService
            reportsService.saveReport(reports);
        } catch (Exception e) {
            return "reports/reportsnew"; // Redirect back to the form on error
        }

        return "redirect:/reports"; // Redirect to the reports list page
    }
}
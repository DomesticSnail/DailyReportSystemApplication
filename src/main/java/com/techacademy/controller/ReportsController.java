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
    public String saveReport(@ModelAttribute("reports") Reports reports, BindingResult result, Authentication authentication) {

        if (result.hasErrors()) {
            return "reports/reportsnew";
        }

        try {
            // Attach the logged-in employee to the report
            UserDetail userDetail = (UserDetail) authentication.getPrincipal();
            reports.setEmployee(userDetail.getEmployee());
            reports.setDeleteFlag(false);

            // Set created_at and updated_at before saving
            LocalDateTime now = LocalDateTime.now();
            reports.setCreatedAt(now);
            reports.setUpdatedAt(now);

            // Use the injected reportsService instance to call the save method
            reportsService.saveReport(reports);
        } catch (Exception e) {
            return "reports/reportsnew"; // Redirect back to the form on error
        }

        return "redirect:/reports"; // Redirect to the reports list page
    }
}
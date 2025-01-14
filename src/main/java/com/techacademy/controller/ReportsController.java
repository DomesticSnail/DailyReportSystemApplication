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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
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
    public String list(Model model, Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal(); // Get the authenticated user
        Employee employee = userDetail.getEmployee(); // Get the Employee object
        String role = employee.getRole().name(); // Access the role and convert it to a string
        // If the user is an admin, show all reports
        if ("ADMIN".equals(role)) {
            model.addAttribute("reportList", reportsService.getAllReports());
            model.addAttribute("listSize", reportsService.getAllReports().size());
        }
        // If the user is general, only show reports they created
        else if ("GENERAL".equals(role)) {
            model.addAttribute("reportList", reportsService.getReportsByEmployee(userDetail.getEmployee()));
            model.addAttribute("listSize", reportsService.getReportsByEmployee(userDetail.getEmployee()).size());
        }

        return "reports/reports";
    }

    @GetMapping("/{id}/detail")
    public String detail(@PathVariable("id") Long id, Model model) {
        Reports report = reportsService.getReportById(id);
        model.addAttribute("report", report);
        return "reports/reportsdetail";
    }

    @GetMapping("/add")
    public String create(@ModelAttribute("reports") Reports reports, Model model, Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String fullName = userDetail.getEmployee().getName();
        model.addAttribute("loggedInUser", fullName);
        model.addAttribute("reports", reports);
        return "reports/reportsnew";
    }

    @PostMapping("/add")
    public String add(
            @Validated Reports reports,
            BindingResult res,
            @AuthenticationPrincipal UserDetail userDetail,
            Model model) {
        String fullName = userDetail.getEmployee().getName();
        model.addAttribute("loggedInUser", fullName);

        if (res.hasErrors()) {
            return "reports/reportsnew";
        }

        ErrorKinds result = reportsService.save(reports, userDetail);
        if (result == ErrorKinds.DATECHECK_ERROR) {
            model.addAttribute("reportDateError", "既に登録されている日付です");
            return "reports/reportsnew";
        }

        return "redirect:/reports";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        reportsService.deleteReportById(id);
        return "redirect:/reports";
    }

    @GetMapping("/{id}/update")
    public String edit(@PathVariable("id") Long id, Model model, @ModelAttribute("report") Reports report, Authentication authentication) {
        // Retrieve the report object to be updated
        report = reportsService.prepareReportForUpdate(id);

        // Add the report object to the model so the form can bind to it
        model.addAttribute("report", report);
        model.addAttribute("employeeName", report.getEmployee().getName());  // Include employee name

        return "reports/reportsupdate";
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable("id") Long id,
            @Validated @ModelAttribute("report") Reports report,
            BindingResult res,
            @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        // If there are validation errors, return to the update form with error messages
        if (res.hasErrors()) {
            model.addAttribute("report", report);
            return "reports/reportsupdate";  // Return to the form with errors
        }

        // Attempt to save and check for errors
        ErrorKinds result = reportsService.save(report, userDetail);  // Check for the DATECHECK_ERROR after saving
        if (result == ErrorKinds.DATECHECK_ERROR) {
            model.addAttribute("report", report);
            model.addAttribute("reportDateError", "既に登録されている日付です");  // Add error message for the date conflict
            return "reports/reportsupdate";  // Return to the form with error message
        }

        // Proceed with the update if no validation errors
        reportsService.updateReport(id, report, userDetail);

        // Redirect to the updated report's detail page
        return "redirect:/reports/" + id + "/detail";
    }
    }
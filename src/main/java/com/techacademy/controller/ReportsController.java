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
        model.addAttribute("reportList", reportsService.getAllReports());
        model.addAttribute("listSize", reportsService.getAllReports().size());
        return "reports/reports";
    }

    @GetMapping("/{id}/detail")
    public String showReportDetail(@PathVariable("id") Long id, Model model) {
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
    public String deleteReport(@PathVariable("id") Long id) {
        reportsService.deleteReportById(id);
        return "redirect:/reports";
    }

    @GetMapping("/{id}/update")
    public String updateReport(@PathVariable("id") Long id, Model model, @ModelAttribute("reports") Reports reports, Authentication authentication) {
        Reports report = reportsService.prepareReportForUpdate(id);
        model.addAttribute("report", report);
        model.addAttribute("employeeName", report.getEmployee().getName());
        return "reports/reportsupdate";
    }

    @PostMapping("/{id}/update")
    public String saveUpdatedReport(
            @PathVariable("id") Long id,
            @Validated Reports reports,
            BindingResult res,
            @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        // Ensure employeeName is added to the model
        String employeeName = userDetail.getEmployee().getName();  // Fetch the name from the authenticated user
        model.addAttribute("employeeName", employeeName);  // Add employeeName to the model

        if (res.hasErrors()) {
            model.addAttribute("report", reports);  // Add the updated report to the model
            return "reports/reportsupdate";  // Return to the update form with error messages
        }

        // Attempt to save and check for errors
        ErrorKinds result = reportsService.save(reports, userDetail);  // Store the result of the save

        // Check if the result is an error
        if (result == ErrorKinds.DATECHECK_ERROR) {
            model.addAttribute("report", reports);  // Re-add report to the model in case of error
            model.addAttribute("reportDateError", "既に登録されている日付です");
            return "reports/reportsupdate";  // Return to the form with error message
        }

        // Proceed with updating the report if no errors
        reportsService.updateReport(id, reports, userDetail);
        return "redirect:/reports/" + id + "/detail";  // Redirect to the updated report's detail page
    }
    }
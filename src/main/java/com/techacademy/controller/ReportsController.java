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

    @GetMapping
    public String showReports(Model model) {
        model.addAttribute("reportList", reportsService.getAllReports());
        model.addAttribute("listSize", reportsService.getAllReports().size());
        return "reports/reports";
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
    public String saveReport(@ModelAttribute("reports") Reports reports, BindingResult result) {
        if (result.hasErrors()) {
            return "errorPage";
        }


        if (reports.getReportDate() != null) {
            reports.setReportDate(reports.getReportDate().toLocalDate().atStartOfDay());
        }

        reportsService.saveReport(reports);

        return "redirect:/reports";
    }
}
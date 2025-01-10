package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.service.ReportsService;

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
        // Fetch all reports and pass to the model
        model.addAttribute("reportList", reportsService.getAllReports());
        model.addAttribute("listSize", reportsService.getAllReports().size());
        return "Employees/reports";
    }
}
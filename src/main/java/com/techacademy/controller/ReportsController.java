package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Reports;
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

// 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute("reports") Reports reports) {
        // Initialize reports if it's null (to avoid errors in the template)
        if (reports == null) {
            reports = new Reports();
        }
        return "employees/reportsnew";  // Your template name
    }


@PostMapping("/add")
public String saveReport(@ModelAttribute("reports") Reports reports, BindingResult result) {
    if (result.hasErrors()) {
        return "errorPage";  // Handle errors appropriately
    }

    // Ensure reportDate is set to start of the day if not already done
    if (reports.getReportDate() != null) {
        reports.setReportDate(reports.getReportDate().toLocalDate().atStartOfDay());
    }

    reportsService.saveReport(reports);
    return "redirect:/reports";
}
}
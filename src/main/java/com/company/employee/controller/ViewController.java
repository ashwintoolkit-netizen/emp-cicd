package com.company.employee.controller;

import com.company.employee.entity.Employee;
import com.company.employee.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewController {

    @Autowired
    private EmployeeService employeeService;

    // Home Page
    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("employees",
                employeeService.getAllEmployees());

        return "index";
    }

    // Show Add Employee Form
    @GetMapping("/add")
    public String showAddForm(Model model) {

        model.addAttribute("employee", new Employee());

        return "add-employee";
    }

    // Save Employee (Add + Update)
    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute Employee employee) {

        employeeService.saveEmployee(employee);

        return "redirect:/";
    }

    // Show Edit Employee Form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Employee employee = employeeService
                .getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        model.addAttribute("employee", employee);

        return "add-employee";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {

      employeeService.deleteEmployee(id);

      return "redirect:/";
   }
  




}

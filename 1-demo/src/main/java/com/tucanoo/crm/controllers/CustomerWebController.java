package com.tucanoo.crm.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tucanoo.crm.data.entities.Customer;
import com.tucanoo.crm.data.repositories.CustomerRepository;
import com.tucanoo.crm.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tucanoo.crm.pdfcreator.EmployeePDFCreator;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerWebController {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerWebController.class);

    @GetMapping
    public String index() {
        return "customer/index";
    }

    private static final String PDF_DIR_PATH = "./pdf";
    
    @GetMapping("/file-explorer")
    public String fileExplorer(Model model) {
        File pdfDir = new File(PDF_DIR_PATH);
        if (pdfDir.exists() && pdfDir.isDirectory()) {
            List<String> fileNames = Arrays.stream(pdfDir.listFiles())
                                           .filter(file -> file.isFile() && file.getName().endsWith(".pdf"))
                                           .map(File::getName)
                                           .collect(Collectors.toList());
            model.addAttribute("fileNames", fileNames);
        } else {
            // Handle the case where the directory does not exist
            model.addAttribute("errorMessage", "Directory not found.");
        }
        return "explorer/index";
    }

    @GetMapping(value = "/data_for_datatable", produces = "application/json")
    @ResponseBody
    public String getDataForDatatable(@RequestParam Map<String, Object> params) {
        int draw = params.containsKey("draw") ? Integer.parseInt(params.get("draw").toString()) : 1;
        int length = params.containsKey("length") ? Integer.parseInt(params.get("length").toString()) : 30;
        int start = params.containsKey("start") ? Integer.parseInt(params.get("start").toString()) : 30;
        int currentPage = start / length;

        String sortName = "id";
        String dataTableOrderColumnIdx = params.get("order[0][column]").toString();
        String dataTableOrderColumnName = "columns[" + dataTableOrderColumnIdx + "][data]";
        if (params.containsKey(dataTableOrderColumnName)) {
            sortName = params.get(dataTableOrderColumnName).toString();
        }
        String sortDir = params.containsKey("order[0][dir]") ? params.get("order[0][dir]").toString() : "asc";

        Sort.Order sortOrder = new Sort.Order((sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC), sortName);
        Sort sort = Sort.by(sortOrder);

        Pageable pageRequest = PageRequest.of(currentPage,
                length,
                sort);

        String queryString = (String) (params.get("search[value]"));

        Page<Customer> customers = customerService.getCustomersForDatatable(queryString, pageRequest);

        long totalRecords = customers.getTotalElements();

        List<Map<String, Object>> cells = new ArrayList<>();
        customers.forEach(customer -> {
            Map<String, Object> cellData = new HashMap<>();
            cellData.put("id", customer.getId());
            cellData.put("firstName", customer.getFirstName());
            cellData.put("lastName", customer.getLastName());
            cellData.put("emailAddress", customer.getEmailAddress());
            cellData.put("city", customer.getCity());
            cellData.put("country", customer.getCountry());
            cellData.put("phoneNumber", customer.getPhoneNumber());
            cells.add(cellData);
        });

        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("draw", draw);
        jsonMap.put("recordsTotal", totalRecords);
        jsonMap.put("recordsFiltered", totalRecords);
        jsonMap.put("data", cells);

        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model) {
        Customer customerInstance = customerRepository.findById(Long.valueOf(id)).get();

        model.addAttribute("customerInstance", customerInstance);

        return "customer/edit";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("customerInstance") Customer customerInstance,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes atts) {
        if (bindingResult.hasErrors()) {
            return "/customer/edit.html";
        } else {
            if (customerRepository.save(customerInstance) != null) {
                atts.addFlashAttribute("message", "Customer updated successfully");
            } else {
                atts.addFlashAttribute("message", "Customer update failed.");
            }

            return "redirect:/customer";
        }
    }

    @GetMapping(value = "/report", produces = "application/json")
    @ResponseBody
    public String report(@RequestParam(defaultValue = "10") int size) {
        int draw = 1;
        int length = 30; // You might want to use these in your logic or remove if unnecessary
        int start = 30;  // You might want to use these in your logic or remove if unnecessary

        // Fetch the page of customers using the size from the query parameter
        Page<Customer> customers = customerService.getAllCustomers(PageRequest.of(0, size));
        long totalRecords = customers.getTotalElements();

        List<Map<String, Object>> cells = new ArrayList<>();
        customers.forEach(customer -> {
            Map<String, Object> cellData = new HashMap<>();
            cellData.put("id", customer.getId());
            cellData.put("firstName", customer.getFirstName());
            cellData.put("lastName", customer.getLastName());
            cellData.put("address", customer.getAddress());
            cellData.put("emailAddress", customer.getEmailAddress());
            cellData.put("city", customer.getCity());
            cellData.put("country", customer.getCountry());
            cellData.put("phoneNumber", customer.getPhoneNumber());
            cells.add(cellData);
        });

        LocalDateTime startTime = LocalDateTime.now();
        EmployeePDFCreator.createPDF(cells);  // Assuming this method exists and is appropriate
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        long milliseconds = duration.toMillis();
        logger.info("Report creation took " + milliseconds + " milliseconds.");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Total Report Time ms", milliseconds);

        return jsonObject.toString();
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("customerInstance", new Customer());
        return "customer/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("customerInstance") Customer customerInstance,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes atts) {
        if (bindingResult.hasErrors()) {
            return "customer/create";
        } else {
            if (customerRepository.save(customerInstance) != null) {
                atts.addFlashAttribute("message", "Customer created successfully");
            } else {
                atts.addFlashAttribute("message", "Customer creation failed.");
            }

            return "redirect:/customer";
        }
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes atts) {
        Customer customerInstance = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer Not Found:" + id));

        customerRepository.delete(customerInstance);

        atts.addFlashAttribute("message", "Customer deleted.");

        return "redirect:/customer";
    }

}

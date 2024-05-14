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
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import com.tucanoo.crm.amq.MessageSender;

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
            return "customer/edit";
        } else {
            if (customerRepository.save(customerInstance) != null) {
                atts.addFlashAttribute("message", "Customer updated successfully");
            } else {
                atts.addFlashAttribute("message", "Customer update failed.");
            }

            return "redirect:/customer";
        }
    }

    @Autowired
    private MessageSender messageSender;

    @GetMapping(value = "/report", produces = "application/json")
    @ResponseBody
    public String report() {
        JsonObject emptyJson = new JsonObject();
        messageSender.sendMessage("generated-reports", emptyJson);
        messageSender.sendMessage("reports", emptyJson);
        int numEmployees = 10;
        Page<Customer> customers = customerService.getAllCustomers(PageRequest.of(0, numEmployees));
        customers.forEach(customer -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", customer.getId());
            json.addProperty("firstName", customer.getFirstName());
            json.addProperty("lastName", customer.getLastName());
            json.addProperty("address", customer.getAddress());
            json.addProperty("emailAddress", customer.getEmailAddress());
            json.addProperty("city", customer.getCity());
            json.addProperty("country", customer.getCountry());
            json.addProperty("phoneNumber", customer.getPhoneNumber());
            messageSender.sendMessage("reports", json);
        });

        
        JsonObject res = new JsonObject();
        res.addProperty("Records added to queue", numEmployees);

        return res.toString();
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

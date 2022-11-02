package com.cgi.dentistapp.controller;

import com.cgi.dentistapp.dto.DentistVisitDTO;
import com.cgi.dentistapp.service.DentistVisitService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class DentistAppController extends WebMvcConfigurerAdapter {
    private static final String[] Dentists = new String[]{"Dr Caroline", "Dr Kennedy", "Dr Michalina", "Dr Logan", "Dr Clare"};
    private static Map<Integer, String> dentistMap;

    static {
        populateDentistMap();
    }

    public DentistAppController(DentistVisitService dentistVisitService) {
        this.dentistVisitService = dentistVisitService;
    }

    private DentistVisitService dentistVisitService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }

    @GetMapping("/")
    public String showRegisterForm(Model model) {
        model.addAttribute("dentistVisitDTO", new DentistVisitDTO());
        model.addAttribute("dentistMap", dentistMap);
        return "form";
    }

    @PostMapping("/")
    public String postRegisterForm(Model model, @Valid DentistVisitDTO dentistVisitDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("dentistVisitDTO", dentistVisitDTO);
            model.addAttribute("dentistMap", dentistMap);
            return "form";
        }
        String dentistName = dentistMap.get(Integer.parseInt(dentistVisitDTO.getDentistName()));
        dentistVisitService.addVisit(
                dentistName,
                dentistVisitDTO.getVisitTime(),
                dentistVisitDTO.getPatientFirstName(),
                dentistVisitDTO.getPatientLastName());
        String message = "You are registered with " + dentistName + " on : " +
                dentistVisitDTO.getVisitTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        model.addAttribute("message", message);
        return "redirect:/results?message=" + message;
    }

    private static void populateDentistMap() {
        dentistMap = new HashMap<>();

        for (int i = 0; i < Dentists.length; i++) {
            dentistMap.put(i + 1, Dentists[i]);
        }
    }
}

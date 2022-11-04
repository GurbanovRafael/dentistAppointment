package com.cgi.dentistapp.controller;

import com.cgi.dentistapp.dto.DentistVisitDTO;
import com.cgi.dentistapp.service.DentistVisitService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class DentistAppController extends WebMvcConfigurerAdapter {
    private static final String[] Dentists = new String[]{"Dr Caroline", "Dr Kennedy", "Dr Michalina", "Dr Logan", "Dr Clare"};
    private static Map<Integer, String> dentistMap;
    private DentistVisitService dentistVisitService;

    private static void populateDentistMap() {
        dentistMap = new HashMap<>();

        for (int i = 0; i < Dentists.length; i++) {
            dentistMap.put(i + 1, Dentists[i]);
        }
    }

    static {
        populateDentistMap();
    }

    private void initModel(Model model) {
        model.addAttribute("appointments", dentistVisitService.findAllAppointments());
        model.addAttribute("dentistVisitDTO", null);
        model.addAttribute("editId", -1);
    }

    public DentistAppController(DentistVisitService dentistVisitService) {
        this.dentistVisitService = dentistVisitService;
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }

    @GetMapping("/")
    public String showRegisterForm(Model model) {
        model.addAttribute("dentistVisitDTO", new DentistVisitDTO());
        model.addAttribute("dentistMap", dentistMap);

        return "form";
    }

    @GetMapping("/results")
    public String showSuccessPage(Model model, @RequestParam(required = false) String message) {
        model.addAttribute("message", message != null ? message : "TODO: success message!");

        return "results";
    }

    @PostMapping("/")
    public String postRegisterForm(Model model, @Valid DentistVisitDTO dentistVisitDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("dentistVisitDTO", dentistVisitDTO);
            model.addAttribute("dentistMap", dentistMap);

            return "form";
        }

        LocalDateTime selectedVisitTime = dentistVisitDTO.getVisitTime();

        // checks if selected visit time is withing an hour range of other appointments
        boolean isOverlapFound = dentistVisitService.findAllAppointments().stream().anyMatch(appointment -> {
            LocalDateTime otherVisitTime = appointment.getVisitTime();
            LocalDateTime otherVisitTimeHourLater = LocalDateTime.of(
                    otherVisitTime.getYear(),
                    otherVisitTime.getMonth(),
                    otherVisitTime.getDayOfMonth(),
                    otherVisitTime.getHour(),
                    otherVisitTime.getMinute()).plusHours(1);


            if (selectedVisitTime.isBefore(otherVisitTimeHourLater)) {
                bindingResult.rejectValue("visitTime", "errors.overlappingVisitTime", "Please pick a later visit time");

                return true;
            }

            return false;
        });

        if (isOverlapFound && bindingResult.hasErrors()) {
            model.addAttribute("dentistVisitDTO", dentistVisitDTO);
            model.addAttribute("dentistMap", dentistMap);

            return "form";
        }

        String dentistName = dentistMap.get(Integer.parseInt(dentistVisitDTO.getDentistName()));

        dentistVisitDTO.setDentistName(dentistName);
        dentistVisitService.addVisit(dentistVisitDTO);

        String message = String.format("You are registered with: %s on %s",
                dentistName,
                dentistVisitDTO.getVisitTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

        model.addAttribute("message", message);

        return String.format("redirect:/results?message=%s", message);
    }

    @GetMapping("/appointments")
    public String showAllAppointments(Model model) {
        initModel(model);

        return "appointments";
    }

    @GetMapping("/appointment/{id}")
    public String showAppointment(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("appointment", dentistVisitService.findAppointment(id));

        return "appointment";
    }

    @GetMapping("/cancel-appointment/{id}")
    public String cancelAppointment(@PathVariable(value = "id") Long id) {
        dentistVisitService.cancelVisit(id);

        return "redirect:/appointments";
    }

    @GetMapping("/cancel")
    public String cancelEdit(Model model) {
        initModel(model);

        return "appointments";
    }

    @GetMapping("/edit/{id}")
    public String editAppointment(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("appointments", dentistVisitService.findAllAppointments());
        model.addAttribute("dentistVisitDTO", new DentistVisitDTO());
        model.addAttribute("dentistMap", dentistMap);
        model.addAttribute("editId", id);

        return "appointments";
    }

    @PutMapping("/update/{id}")
    public String updateAppointment(
            Model model,
            @PathVariable(value = "id") Long id,
            @Valid DentistVisitDTO dentistVisitDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("dentistVisitDTO", dentistVisitDTO);
            model.addAttribute("dentistMap", dentistMap);

            return "form";
        }

        String dentistName = dentistMap.get(Integer.parseInt(dentistVisitDTO.getDentistName()));

        dentistVisitDTO.setId(id);
        dentistVisitDTO.setDentistName(dentistName);

        dentistVisitService.updateVisit(dentistVisitDTO);

        initModel(model);

        return "appointments";
    }
}

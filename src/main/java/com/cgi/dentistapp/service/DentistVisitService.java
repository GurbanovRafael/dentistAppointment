package com.cgi.dentistapp.service;

import com.cgi.dentistapp.entity.DentistVisitEntity;
import com.cgi.dentistapp.repository.DentistVisitRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class DentistVisitService {
    public DentistVisitService(DentistVisitRepository dentistVisitRepository) {
        this.dentistVisitRepository = dentistVisitRepository;
    }

    private DentistVisitRepository dentistVisitRepository;

    public void addVisit(String dentistName, LocalDateTime visitTime, String patientFirstName, String patientLastName) {
        DentistVisitEntity dentistVisitEntity = new DentistVisitEntity();
        dentistVisitEntity.setVisitTime(visitTime);
        dentistVisitEntity.setDoctorName(dentistName);
        dentistVisitEntity.setPatientFirstName(patientFirstName);
        dentistVisitEntity.setPatientLastName(patientLastName);
        dentistVisitRepository.save(dentistVisitEntity);
    }
}

package com.cgi.dentistapp.service;

import com.cgi.dentistapp.dto.DentistVisitDTO;
import com.cgi.dentistapp.entity.DentistVisitEntity;
import com.cgi.dentistapp.repository.DentistVisitRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DentistVisitService {
    public DentistVisitService(DentistVisitRepository dentistVisitRepository) {
        this.dentistVisitRepository = dentistVisitRepository;
    }

    private DentistVisitRepository dentistVisitRepository;

    public void addVisit(DentistVisitDTO newVisitDto) {
        DentistVisitEntity newVisitEntity = new DentistVisitEntity();
        newVisitEntity.setVisitTime(newVisitDto.getVisitTime());
        newVisitEntity.setDentistName(newVisitDto.getDentistName());
        newVisitEntity.setPatientFirstName(newVisitDto.getPatientFirstName());
        newVisitEntity.setPatientLastName(newVisitDto.getPatientLastName());
        dentistVisitRepository.save(newVisitEntity);
    }

    public List<DentistVisitEntity> findAllAppointments() {
        return dentistVisitRepository.findAll();
    }

    public DentistVisitEntity findAppointment(Long id) {
        return dentistVisitRepository.findOne(id);
    }

    public void cancelVisit(Long id) {
        dentistVisitRepository.delete(id);
    }

    public void updateVisit(DentistVisitDTO visitDto) {
        DentistVisitEntity resultEntity;

        if (dentistVisitRepository.exists(visitDto.getId())) {
            resultEntity = dentistVisitRepository.findOne(visitDto.getId());
        } else {
            resultEntity = new DentistVisitEntity();
        }

        resultEntity.setVisitTime(visitDto.getVisitTime());
        resultEntity.setDentistName(visitDto.getDentistName());
        resultEntity.setPatientFirstName(visitDto.getPatientFirstName());
        resultEntity.setPatientLastName(visitDto.getPatientLastName());
        dentistVisitRepository.save(resultEntity);
    }
}

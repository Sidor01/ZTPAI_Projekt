package org.example.skillwheel.service;

import org.example.skillwheel.controller.InstructorController;
import org.example.skillwheel.model.Instructor;
import org.example.skillwheel.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

    @Autowired
    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public Optional<Instructor> getInstructorById(Long id) {
        return instructorRepository.findById(id);
    }

    public Instructor addInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Optional<Instructor> updateInstructor(Long id, Instructor updatedInstructor) {
        return instructorRepository.findById(id).map(instructor -> {
            instructor.setName(updatedInstructor.getName());
            instructor.setSurname(updatedInstructor.getSurname());
            instructor.setEmail(updatedInstructor.getEmail());
            instructor.setPassword(updatedInstructor.getPassword());
            return instructorRepository.save(instructor);
        });
    }

    public boolean deleteInstructor(Long id) {
        return instructorRepository.findById(id).map(instructor -> {
            instructorRepository.delete(instructor);
            return true;
        }).orElse(false);
    }
}
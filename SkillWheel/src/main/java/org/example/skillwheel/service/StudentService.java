package org.example.skillwheel.service;

import org.example.skillwheel.model.Student;
import org.example.skillwheel.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private static final Logger LOGGER = Logger.getLogger(StudentService.class.getName());

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> updateStudent(Long id, Student updatedStudent) {
        return studentRepository.findById(id)
                .map(student -> {
                    LOGGER.info("Updating student with ID: " + id);
                    LOGGER.info("New data: " + updatedStudent);
                    student.setName(updatedStudent.getName());
                    student.setSurname(updatedStudent.getSurname());
                    student.setEmail(updatedStudent.getEmail());
                    student.setPassword(updatedStudent.getPassword());
                    student.setNameOfSchool(updatedStudent.getNameOfSchool());
                    return studentRepository.save(student);
                });
    }

    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

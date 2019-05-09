package com.example.tutor.student;

import com.example.tutor.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class StudentController {

    private StudentRepository studentRepository;

    StudentController(StudentRepository repository) {
        this.studentRepository = repository;
    }

    @GetMapping("/students")
    public Page<Student> getStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @GetMapping("/students/{student_id}")
    public Student getStudent(@PathVariable Integer student_id) {
        return studentRepository.findById(student_id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + student_id));
    }

    @PostMapping("/students")
    public Student createStudent(@Valid @RequestBody Student student) {
        return studentRepository.save(student);
    }

    @PutMapping("/students/{student_id}")
    public Student updateStudent(@PathVariable Integer student_id,
                                      @Valid @RequestBody Student studentRequest) {

        return studentRepository.findById(student_id)
                .map(student -> {
                    student.setYear(studentRequest.getYear());
                    return studentRepository.save(student);
                }).orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + student_id));
    }


    @DeleteMapping("/students/{student_id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer student_id) {
        return studentRepository.findById(student_id)
                .map(student -> {
                    studentRepository.delete(student);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + student_id));
    }
}
package com.example.tutor.student;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Student")
@Table(name = "students")
public class Student implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(columnDefinition = "year")
    private Integer year;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
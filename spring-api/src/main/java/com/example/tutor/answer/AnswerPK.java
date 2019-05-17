package com.example.tutor.answer;

import com.example.tutor.question.Question;
import com.example.tutor.student.Student;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class AnswerPK implements Serializable {
    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "answer_date")
    private LocalDateTime date;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public AnswerPK() {

    }

    public AnswerPK(LocalDateTime d, Student s, Question q) {
        this.date = d;
        this.student = s;
        this.question = q;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnswerPK o = (AnswerPK) obj;
        return this.question.getId() == o.getQuestion().getId() && this.student.getId() == o.getStudent().getId() && this.date == o.getDate();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
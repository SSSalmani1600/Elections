package nl.hva.election_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "elections")
public class ElectionEntity {

    @Id
    private int year;

    private String name;
    private String election_code;
    private LocalDate date;

    public ElectionEntity() {
    }

    public ElectionEntity(int year, String election_code, String name, LocalDate date) {
        this.year = year;
        this.election_code = election_code;
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }


    public String getElection_code() {
        return election_code;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setElection_code(String election_code) {
        this.election_code = election_code;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

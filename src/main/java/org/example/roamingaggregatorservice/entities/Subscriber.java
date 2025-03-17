package org.example.roamingaggregatorservice.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Subscribers")
public class Subscriber {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

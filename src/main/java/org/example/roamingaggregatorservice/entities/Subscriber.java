package org.example.roamingaggregatorservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "subscribers")
@Schema(description = "Сущность абонента")
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор абонента", example = "1")
    private Long id;

    @Column(name = "msisdn", unique = true, nullable = false)
    @Schema(description = "Мобильный номер абонента (MSISDN)", example = "79123456789")
    private String msisdn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return Objects.equals(id, that.id) && Objects.equals(msisdn, that.msisdn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, msisdn);
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "id=" + id +
                ", msisdn='" + msisdn + '\'' +
                '}';
    }
}

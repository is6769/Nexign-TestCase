package org.example.roamingaggregatorservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

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
}

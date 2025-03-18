package org.example.roamingaggregatorservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sdrs")
@Schema(description = "Запись данных вызова (Call Data Record)")
public class Cdr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор записи", example = "1")
    private Long id;

    @Column(name = "call_type", nullable = false)
    @Schema(description = "Тип вызова (01 - входящий, 02 - исходящий)", example = "01")
    private String callType;

    @Column(name = "caller_number", nullable = false)
    @Schema(description = "Номер вызывающего абонента", example = "79123456789")
    private String callerNumber;

    @Column(name = "called_number", nullable = false)
    @Schema(description = "Номер вызываемого абонента", example = "79876543210")
    private String calledNumber;

    @Column(name = "start_date_time", nullable = false)
    @Schema(description = "Время начала вызова", example = "2023-01-15T14:30:15")
    private LocalDateTime startDateTime;

    @Column(name = "finish_date_time", nullable = false)
    @Schema(description = "Время завершения вызова", example = "2023-01-15T14:35:45")
    private LocalDateTime finishDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getFinishDateTime() {
        return finishDateTime;
    }

    public void setFinishDateTime(LocalDateTime finishDateTime) {
        this.finishDateTime = finishDateTime;
    }
}

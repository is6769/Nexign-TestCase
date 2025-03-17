package org.example.roamingaggregatorservice.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CDRs")
public class Cdr {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "call_type", nullable = false)
    private String callType;

    @Column(name = "caller_number", nullable = false)
    private String callerNumber;

    @Column(name = "called_number", nullable = false)
    private String calledNumber;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "finish_date_time", nullable = false)
    private LocalDateTime finishDateTime;

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

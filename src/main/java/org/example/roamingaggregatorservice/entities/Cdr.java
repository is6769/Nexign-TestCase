package org.example.roamingaggregatorservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность "Запись данных вызова" (Call Data Record - CDR).
 * <p>
 * Содержит информацию об одном телефонном звонке между двумя абонентами,
 * включая время начала и окончания звонка, типа звонка, а также
 * номера вызывающего и вызываемого абонентов.
 * </p>
 * 
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
@Entity
@Table(name = "cdrs")
@Schema(description = "Запись данных вызова (Call Data Record)")
public class Cdr {

    /**
     * Уникальный идентификатор записи CDR.
     * <p>
     * Автоматически генерируется базой данных при сохранении.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор записи", example = "1")
    private Long id;

    /**
     * Тип вызова.
     * <p>
     * 01 - входящий вызов, 02 - исходящий вызов.
     * </p>
     */
    @Column(name = "call_type", nullable = false)
    @Schema(description = "Тип вызова (01 - входящий, 02 - исходящий)", example = "01")
    private String callType;

    /**
     * Номер телефона вызывающего абонента.
     */
    @Column(name = "caller_number", nullable = false)
    @Schema(description = "Номер вызывающего абонента", example = "79123456789")
    private String callerNumber;

    /**
     * Номер телефона вызываемого абонента.
     */
    @Column(name = "called_number", nullable = false)
    @Schema(description = "Номер вызываемого абонента", example = "79876543210")
    private String calledNumber;

    /**
     * Дата и время начала вызова.
     */
    @Column(name = "start_date_time", nullable = false)
    @Schema(description = "Время начала вызова", example = "2023-01-15T14:30:15")
    private LocalDateTime startDateTime;

    /**
     * Дата и время окончания вызова.
     */
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cdr cdr = (Cdr) o;
        return Objects.equals(id, cdr.id) && Objects.equals(callType, cdr.callType) && Objects.equals(callerNumber, cdr.callerNumber) && Objects.equals(calledNumber, cdr.calledNumber) && Objects.equals(startDateTime, cdr.startDateTime) && Objects.equals(finishDateTime, cdr.finishDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, callType, callerNumber, calledNumber, startDateTime, finishDateTime);
    }

    @Override
    public String toString() {
        return "Cdr{" +
                "id=" + id +
                ", callType='" + callType + '\'' +
                ", callerNumber='" + callerNumber + '\'' +
                ", calledNumber='" + calledNumber + '\'' +
                ", startDateTime=" + startDateTime +
                ", finishDateTime=" + finishDateTime +
                '}';
    }
}

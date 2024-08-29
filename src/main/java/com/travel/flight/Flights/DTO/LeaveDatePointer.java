package com.travel.flight.Flights.DTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "leave_date_pointer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDatePointer {
    @Id
    private String id;

    private String lastProcessedLeaveDate;
}

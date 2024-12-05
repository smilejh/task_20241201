package com.ljh.task.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CsvBindByPosition(position = 0)
    @Column
    private String name;

    @CsvBindByPosition(position = 1)
    @Column
    private String email;

    @CsvBindByPosition(position = 2)
    @Column
    private String tel;

    @CsvCustomBindByPosition(position = 3, converter = LocalDateConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column
    private LocalDate joined;


    public static class LocalDateConverter extends AbstractBeanField<LocalDate, String> {
        private static final DateTimeFormatter FORMATTER =
                DateTimeFormatter.ofPattern("yyyy.MM.dd");

        @Override
        protected LocalDate convert(String value) throws DateTimeParseException {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(value, FORMATTER);
        }
    }

}

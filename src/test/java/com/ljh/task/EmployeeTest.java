package com.ljh.task;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljh.task.domain.Employee;
import com.opencsv.CSVWriter;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static List<Employee> expectEmployeeList;


    @BeforeEach
    void setUp() {
        expectEmployeeList = new ArrayList<>();
        expectEmployeeList.add(Employee.builder().id(1L).name("김철수").email("test1@tset.com")
                .tel("01012345678").joined(LocalDate.parse("2024-12-01")).build());
        expectEmployeeList.add(Employee.builder().id(2L).name("김영희").email("test2@tset.com")
                .tel("01023456789").joined(LocalDate.parse("2024-12-02")).build());
        expectEmployeeList.add(Employee.builder().id(3L).name("김영수").email("test3@tset.com")
                .tel("01034567890").joined(LocalDate.parse("2024-12-03")).build());
    }


    @Test
    void getAllEmployeesNotFoundTest() {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/employee").param("page", "0")
                    .param("pageSize", "10")).andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAllEmployeesOkTest() {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/employee").param("page", "0")
                    .param("pageSize", "10")).andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void getEmployeeNotFoundTest() {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/employee/{name}", "이철수"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEmployeeOkTest() {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/employee/{name}", "김철수"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveEmployeeJsonTest() {
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/employee").contentType(MediaType.TEXT_PLAIN)
                            .content(objectMapper.writeValueAsString(expectEmployeeList)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveEmployeeCsvTest() {
        try (StringWriter writer = new StringWriter();
                CSVWriter csvWriter = new CSVWriter(writer)) {

            for (Employee employee : expectEmployeeList) {
                csvWriter
                        .writeNext(
                                new String[] {employee.getName(), employee.getEmail(),
                                        employee.getTel(),
                                        employee.getJoined()
                                                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))},
                                false);
            }

            mockMvc.perform(MockMvcRequestBuilders.post("/api/employee")
                    .contentType(MediaType.TEXT_PLAIN).content(writer.toString()))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

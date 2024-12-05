package com.ljh.task.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljh.task.domain.Employee;
import com.ljh.task.service.EmployeeService;
import com.ljh.task.util.ValidUtil;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Employee API",
        version = "1.0", description = "Employee API 명세서"))
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;
    private final ValidUtil validUtil;

    @Operation(summary = "사원 목록 조회", description = "사원 목록을 조회합니다.",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "조회 실패")})
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<Page<Employee>> getAllEmployees(@RequestParam int page,
            @RequestParam int pageSize) {
        Page<Employee> employeePage = employeeService.findAll(page, pageSize);
        if (ObjectUtils.isEmpty(employeePage) || employeePage.getTotalElements() <= 0) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(employeePage);
        }
    }

    @Operation(summary = "사원 이름으로 조회", description = "사원 이름으로 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")})
    @GetMapping(path = "/{name}", produces = "application/json")
    public ResponseEntity<List<Employee>> getEmployee(@PathVariable String name) {
        List<Employee> employeeList = employeeService.findAllByName(name);
        if (ObjectUtils.isEmpty(employeeList)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(employeeList);
        }
    }

    @Operation(summary = "사원 Data 업로드", description = "사원 Data를 업로드합니다.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "업로드 실패")})
    @PostMapping(path = "", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<String> saveEmployeeFile(
            @RequestParam(name = "file", required = true) MultipartFile file) {

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest().body("파일 이름이 없습니다.");
        }

        String fileContent;
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            if (fileName.endsWith(".json")) {
                fileContent = reader.lines().collect(Collectors.joining("\n"));
                try {
                    List<Employee> employeeList = objectMapper.readValue(fileContent,
                            new TypeReference<List<Employee>>() {});
                    employeeService.saveAll(employeeList);
                    return ResponseEntity.status(201).body("JSON 파일이 성공적으로 업로드되었습니다");
                } catch (JsonProcessingException e) {
                    log.error("JSON 파싱 오류: {}", e.getMessage());
                    return ResponseEntity.internalServerError()
                            .body("JSON 파싱 오류 : " + e.getMessage());
                }
            } else if (fileName.endsWith(".csv")) {
                try {
                    fileContent = reader.lines().collect(Collectors.joining("\n"));
                    CsvToBean<Employee> csvToBean =
                            new CsvToBeanBuilder<Employee>(new StringReader(fileContent))
                                    .withType(Employee.class).withIgnoreLeadingWhiteSpace(true)
                                    .withThrowExceptions(true).build();

                    List<Employee> employeeList = csvToBean.parse();
                    employeeService.saveAll(employeeList);

                    return ResponseEntity.status(201).body("CSV 파일이 성공적으로 업로드되었습니다");
                } catch (Exception e) {
                    log.error("CSV 파싱 오류: {}", e.getMessage());
                    return ResponseEntity.internalServerError()
                            .body("CSV 파싱 오류 : " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("지원되지 않는 파일 형식입니다.");
            }
        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생: " + e.getMessage());
            return ResponseEntity.internalServerError().body("파일 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    @Operation(summary = "사원 Data 업로드", description = "사원 Data를 업로드합니다.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "업로드 실패")})
    @PostMapping(path = "", consumes = "text/plain", produces = "application/json")
    public ResponseEntity<String> saveEmployeeText(@RequestBody(required = true) String data) {
        log.debug("RequestBody : {}", data);
        if (validUtil.isValidJson(data)) {
            try {
                List<Employee> employeeList =
                        objectMapper.readValue(data, new TypeReference<List<Employee>>() {});
                employeeService.saveAll(employeeList);
                return ResponseEntity.status(201).build();
            } catch (JsonProcessingException e) {
                log.error("JSON 파싱 오류: {}", e.getMessage());
                return ResponseEntity.internalServerError().body("JSON 파싱 오류 : " + e.getMessage());
            }

        } else if (validUtil.isValidCsv(data)) {
            try {
                CsvToBean<Employee> csvToBean =
                        new CsvToBeanBuilder<Employee>(new StringReader(data))
                                .withType(Employee.class).withIgnoreLeadingWhiteSpace(true).build();

                List<Employee> employeeList = csvToBean.parse();
                employeeService.saveAll(employeeList);
                return ResponseEntity.status(201).build();
            } catch (Exception e) {
                log.error("CSV 파싱 오류: {}", e.getMessage());
                return ResponseEntity.internalServerError().body("CSV 파싱 오류 : " + e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("유효한 JSON 또는 CSV 형식이 아닙니다.");
        }
    }
}

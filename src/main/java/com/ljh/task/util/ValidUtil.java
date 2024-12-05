package com.ljh.task.util;

import java.io.IOException;
import java.io.StringReader;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ValidUtil {
    private final ObjectMapper objectMapper;

    public boolean isValidJson(String data) {
        try {
            objectMapper.readTree(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidCsv(String data) {
        try (CSVReader csvReader = new CSVReader(new StringReader(data))) {
            String[] headers = csvReader.readNext();
            if (headers == null || headers.length < 2) {
                return false; // 헤더가 없거나 열이 2개 미만인 경우 CSV가 아님
            }

            // 첫 번째 줄 이후의 데이터 줄을 검사
            while ((csvReader.readNext()) != null) {
                // 기본적으로 CSV 형식이 유효하면 계속 읽기
            }
            return true; // 모든 줄이 유효한 경우
        } catch (CsvValidationException | IOException e) {
            return false; // CSV 파싱 중 오류 발생 시 CSV가 아님
        }
    }
}

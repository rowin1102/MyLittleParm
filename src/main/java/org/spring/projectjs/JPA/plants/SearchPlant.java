package org.spring.projectjs.JPA.plants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchPlant {
    private List<String[]> rows = new ArrayList<>();

    public SearchPlant(String csvPath, String encoding) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvPath), encoding))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                rows.add(values);
            }
        }
    }

    // 식물명으로 행 전체 찾기
    public List<String[]> findPlantByName(String name) {
        List<String[]> result = new ArrayList<>();
        // 첫 줄은 헤더
        for (int i = 1; i < rows.size(); i++) {
            if (rows.get(i)[0].trim().equals(name)) { // 첫 번째 컬럼(name) 기준
                result.add(rows.get(i));
            }
        }
        return result;
    }

    public String[] getHeader() {
        return rows.get(0);
    }
    public List<String[]> getRows() {
        return rows;
    }
}

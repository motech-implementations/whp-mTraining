package org.motechproject.whp.mtraining.web.parser;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Component
public class CsvParser {

    public <T> List<T> parse(MultipartFile multipartFile, Class<T> type) throws IOException {
        StringReader reader = new StringReader(new String(multipartFile.getBytes()));
        HeaderColumnNameTranslateMappingStrategy<T> columnNameMappingStrategy = getColumnMappingStrategy(type);
        CsvToBean<T> csvToBean = new CsvToBean<>();
        return csvToBean.parse(columnNameMappingStrategy, new CSVReader(reader));
    }

    private <T> HeaderColumnNameTranslateMappingStrategy<T> getColumnMappingStrategy(Class<T> type) {
        HeaderColumnNameTranslateMappingStrategy<T> columnNameMappingStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
        columnNameMappingStrategy.setType(type);
        columnNameMappingStrategy.setColumnMapping(getColumnMapping(type));
        return columnNameMappingStrategy;
    }

    private Map<String, String> getColumnMapping(Class type) {
        Map<String, String> mapping = new HashMap<>();
        List<Field> fields = asList(type.getDeclaredFields());
        for (Field field : fields) {
            mapping.put(field.getName(), field.getName());
        }
        return mapping;
    }
}

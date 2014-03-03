package org.motechproject.whp.mtraining.web.parser;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.*;

import static java.util.Arrays.asList;

@Component
public class CsvParser {

    public <T> List<T> parse(MultipartFile multipartFile, Class<T> type) throws IOException {
        StringReader reader = new StringReader(new String(multipartFile.getBytes()));
        HeaderColumnNameTranslateMappingStrategy<T> columnNameMappingStrategy = getColumnMappingStrategy(type);
        CsvToBean<T> csvToBean = new CsvToBean<>();
        Set<String> columnNames = columnNameMappingStrategy.getColumnMapping().keySet();
        throwExceptionForMissingRequiredHeaders(new StringReader(new String(multipartFile.getBytes())), columnNames);
        return csvToBean.parse(columnNameMappingStrategy, new CSVReader(reader));
    }

    private void throwExceptionForMissingRequiredHeaders(StringReader reader, Set<String> columnNames) throws IOException {
        CSVReader csvReader = new CSVReader(reader);
        List<String> headings = Arrays.asList(csvReader.readNext());
        headings = convertElementsOfArrayToLowerCase(headings);
        for (String columnName : columnNames) {
            if (!headings.contains(columnName.toLowerCase()))
                throw new RuntimeException("The column:"+columnName+" is missing in the CSV. Please add this column and try importing again.");
        }
    }

    private <T> HeaderColumnNameTranslateMappingStrategy<T> getColumnMappingStrategy(Class<T> type) {
        HeaderColumnNameTranslateMappingStrategy<T> columnNameMappingStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
        columnNameMappingStrategy.setType(type);
        columnNameMappingStrategy.setColumnMapping(getColumnMapping(type));
        return columnNameMappingStrategy;
    }

    private List<String> convertElementsOfArrayToLowerCase(List<String> headings) {
        List<String> headingsWithSmallCase = new ArrayList<>();
        for (String heading : headings) {
            headingsWithSmallCase.add(heading.toLowerCase());
        }
        return headingsWithSmallCase;
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

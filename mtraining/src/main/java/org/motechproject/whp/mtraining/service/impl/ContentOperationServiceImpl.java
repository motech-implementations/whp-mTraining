package org.motechproject.whp.mtraining.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service("contentOperationService")
public class ContentOperationServiceImpl implements ContentOperationService {

    private static final Logger LOG = LoggerFactory.getLogger(ContentOperationServiceImpl.class);

    public static final String DESCRIPTION_MAPPING_NAME = "description";
    public static final String FILENAME_MAPPING_NAME = "filename";

    @Override
    public String getFileNameFromContentString(String content){
        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(content,
                    new TypeReference<HashMap<String,String>>(){});
        } catch (IOException e) {
            LOG.warn("Content isn't coded in JSON correctly");
            return null;
        }
        return map.get(FILENAME_MAPPING_NAME);
    }

    @Override
    public String getDescriptionFromContentString(String content){
        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(content,
                    new TypeReference<HashMap<String,String>>(){});
        } catch (IOException e) {
            LOG.warn("Content isn't coded in JSON correctly");
            return null;
        }
        return map.get(DESCRIPTION_MAPPING_NAME);
    }

    @Override
    public void getFileNameAndDescriptionFromContent(CourseUnitMetadataDto courseUnitMetadataDto, String content) {
        courseUnitMetadataDto.setDescription(getDescriptionFromContentString(content));
        courseUnitMetadataDto.setFilename(getFileNameFromContentString(content));
    }

    @Override
    public String codeFileNameAndDescriptionIntoContent(String filename, String description) {
        ObjectMapper mapper = new ObjectMapper();
        String content = "";
        Map<String, String> map = new HashMap<String, String>();

        map.put("filename", filename);
        map.put("description", description);
        try {
            content = mapper.writeValueAsString(map);
        } catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

}

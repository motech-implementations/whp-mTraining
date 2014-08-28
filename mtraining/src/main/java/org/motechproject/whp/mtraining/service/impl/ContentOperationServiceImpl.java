package org.motechproject.whp.mtraining.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("contentOperationService")
public class ContentOperationServiceImpl implements ContentOperationService {

    private static final Logger LOG = LoggerFactory.getLogger(ContentOperationServiceImpl.class);

    public static final String DESCRIPTION_MAPPING_NAME = "description";
    public static final String FILENAME_MAPPING_NAME = "filename";
    public static final String QUESTION_MAPPING_NAME = "name";
    public static final String ANSWER_MAPPING_NAME = "answer";
    public static final String OPTIONS_MAPPING_NAME = "options";
    public static final String ANSWER_FILENAME_MAPPING_NAME = "answerFilename";
    public static final String CONTENT_ID_MAPPING_NAME = "contentId";

    @Override
    public void getFileNameAndDescriptionFromContent(CourseUnitMetadataDto courseUnitMetadataDto, String content) {
        courseUnitMetadataDto.setDescription(getFromJsonString(content, DESCRIPTION_MAPPING_NAME));
        courseUnitMetadataDto.setExternalId(getFromJsonString(content, FILENAME_MAPPING_NAME));
    }

    @Override
    public String codeIntoContent(String filename, String description, UUID uuid) {
        String content = new String();
        content = codeIntoJsonString(content, FILENAME_MAPPING_NAME, filename);
        content = codeIntoJsonString(content, DESCRIPTION_MAPPING_NAME, description);
        content = codeIntoJsonString(content, CONTENT_ID_MAPPING_NAME, uuid.toString());
        return content;
    }


    @Override
    public void getQuestionNameAndDescriptionFromQuestion(QuestionDto questionDto, String question) {
        questionDto.setName(getFromJsonString(question, QUESTION_MAPPING_NAME));
        questionDto.setDescription(getFromJsonString(question, DESCRIPTION_MAPPING_NAME));
    }

    @Override
    public String codeIntoQuestion(String questionName, String description, UUID uuid) {
        String question = new String();
        question = codeIntoJsonString(question, QUESTION_MAPPING_NAME, questionName);
        question = codeIntoJsonString(question, DESCRIPTION_MAPPING_NAME, description);
        question = codeIntoJsonString(question, CONTENT_ID_MAPPING_NAME, uuid.toString());
        return  question;
    }

    @Override
    public void getAnswersAndFilesNamesFromAnswer(QuestionDto questionDto, String answer) {
        questionDto.setCorrectOption(getFromJsonString(answer, ANSWER_MAPPING_NAME));
        questionDto.setOptions(getFromJsonString(answer, OPTIONS_MAPPING_NAME));
        questionDto.setExternalId(getFromJsonString(answer, FILENAME_MAPPING_NAME));
        questionDto.setExplainingAnswerFilename(getFromJsonString(answer, ANSWER_FILENAME_MAPPING_NAME));
    }

    @Override
    public String codeAnswersAndFilesNamesIntoAnswer(String correctOption, String options, String filename, String explainingAnswerFilename) {
        String answer = "";
        answer = codeIntoJsonString(answer, ANSWER_MAPPING_NAME, correctOption);
        answer = codeIntoJsonString(answer, OPTIONS_MAPPING_NAME, options);
        answer = codeIntoJsonString(answer, FILENAME_MAPPING_NAME, filename);
        answer = codeIntoJsonString(answer, ANSWER_FILENAME_MAPPING_NAME, explainingAnswerFilename);
        return  answer;
    }

    @Override
    public UUID getUuidFromJsonString(String content) {
        return UUID.fromString(getFromJsonString(content, CONTENT_ID_MAPPING_NAME));
    }

    private String getFromJsonString(String jsonString, String mappingName) {
        if (jsonString == null){
            return null;
        }
        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(jsonString,
                    new TypeReference<HashMap<String,String>>(){});
        } catch (IOException e) {
            LOG.error("mtraining.error.getFromJsonString" + e.getMessage());
            return null;
        }
        return map.get(mappingName);
    }

    private String codeIntoJsonString(String jsonString, String mappingName, String value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode;
        if (jsonString.isEmpty()){
            objectNode = new ObjectNode(JsonNodeFactory.instance);
        }
        else {
            try {
                objectNode = (ObjectNode) mapper.readTree(jsonString);
            } catch (IOException e) {
                LOG.error("mtraining.error.codeIntoJsonString" + e.getMessage());
                return jsonString;
            }
        }
        objectNode.put(mappingName, value);
        return objectNode.toString();
    }
}

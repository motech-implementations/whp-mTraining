package org.motechproject.whp.mtraining.service.impl;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static final String NO_OF_QUESTIONS_MAPPING_NAME = "noOfQuestionsToBePlayed";
    public static final String VERSION_MAPPING_NAME = "version";

    @Override
    public void getMetadataFromContent(CourseUnitMetadataDto courseUnitMetadataDto, String content) {
        courseUnitMetadataDto.setDescription(getFromJsonString(content, DESCRIPTION_MAPPING_NAME));
        courseUnitMetadataDto.setExternalId(getFromJsonString(content, FILENAME_MAPPING_NAME));
        courseUnitMetadataDto.setVersion(getVersionFromJsonString(content));
    }

    @Override
    public String codeIntoContent(String filename, String description, UUID uuid, int version) {
        String content = "";
        content = codeIntoJsonString(content, FILENAME_MAPPING_NAME, filename);
        content = codeIntoJsonString(content, DESCRIPTION_MAPPING_NAME, description);
        content = codeIntoJsonString(content, CONTENT_ID_MAPPING_NAME, uuid.toString());
        content = codeIntoJsonString(content, VERSION_MAPPING_NAME, Integer.valueOf(version).toString());
        return content;
    }

    @Override
    public String codeIntoQuizContent(String description, UUID uuid, int version, int noOfQuestionsToBePlayed) {
        String content = codeIntoContent(null, description, uuid, version);
        content = codeIntoJsonString(content, NO_OF_QUESTIONS_MAPPING_NAME, String.valueOf(noOfQuestionsToBePlayed));
        return content;
    }


    @Override
    public void getQuestionNameAndDescriptionFromQuestion(QuestionDto questionDto, String question) {
        questionDto.setName(getFromJsonString(question, QUESTION_MAPPING_NAME));
        questionDto.setDescription(getFromJsonString(question, DESCRIPTION_MAPPING_NAME));
    }

    @Override
    public String codeIntoQuestion(String questionName, String description, UUID uuid, int version) {
        String question = "";
        question = codeIntoJsonString(question, QUESTION_MAPPING_NAME, questionName);
        question = codeIntoJsonString(question, DESCRIPTION_MAPPING_NAME, description);
        question = codeIntoJsonString(question, CONTENT_ID_MAPPING_NAME, uuid.toString());
        question = codeIntoJsonString(question, VERSION_MAPPING_NAME, Integer.valueOf(version).toString());
        return  question;
    }

    @Override
    public void getAnswersAndFilesNamesFromAnswer(QuestionDto questionDto, String answer) {
        questionDto.setCorrectOption(getFromJsonString(answer, ANSWER_MAPPING_NAME));
        questionDto.setOptions(getIntegerListFromJsonString(answer, OPTIONS_MAPPING_NAME));
        questionDto.setExternalId(getFromJsonString(answer, FILENAME_MAPPING_NAME));
        questionDto.setExplainingAnswerFilename(getFromJsonString(answer, ANSWER_FILENAME_MAPPING_NAME));
    }

    @Override
    public String codeAnswersAndFilesNamesIntoAnswer(String correctOption, List<Integer> options, String filename, String explainingAnswerFilename) {
        String answer = "";
        answer = codeIntoJsonString(answer, ANSWER_MAPPING_NAME, correctOption);
        answer = codeIntegerListIntoJsonString(answer, OPTIONS_MAPPING_NAME, options);
        answer = codeIntoJsonString(answer, FILENAME_MAPPING_NAME, filename);
        answer = codeIntoJsonString(answer, ANSWER_FILENAME_MAPPING_NAME, explainingAnswerFilename);
        return  answer;
    }

    @Override
    public UUID getUuidFromJsonString(String content) {
        return UUID.fromString(getFromJsonString(content, CONTENT_ID_MAPPING_NAME));
    }

    @Override
    public int getVersionFromJsonString(String content) {
        String version = getFromJsonString(content, VERSION_MAPPING_NAME);
        if (version != null) {
            return Integer.valueOf(version);
        } else {
            return 0;
        }
    }

    private List<Integer> getIntegerListFromJsonString(String jsonString, String mappingName) {
        if (jsonString == null){
            return null;
        }
        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode;
        List<Integer> integerList = new ArrayList<>();

        try {
            arrayNode = (ArrayNode)mapper.readTree(jsonString).get(mappingName);
        } catch (IOException e) {
            LOG.info("Content isn't coded in JSON correctly" + e.getMessage());
            return null;
        }
        for (final JsonNode objNode : arrayNode) {
            integerList.add(objNode.getIntValue());
        }
        return integerList;
    }

    private String getFromJsonString(String jsonString, String mappingName) {
        if (jsonString == null){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonString).get(mappingName).getTextValue();
        } catch (Exception e) {
            LOG.info("Exception while getting " + mappingName + " from string: " + jsonString + "" +
                    "\nContent isn't coded in JSON correctly, " + e.getMessage());
            return null;
        }
    }

    private String codeIntoJsonString(String jsonString, String mappingName, String value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode;
        try {
            if (jsonString.isEmpty()) {
                objectNode = new ObjectNode(JsonNodeFactory.instance);
            }
            else {
                objectNode = (ObjectNode) mapper.readTree(jsonString);
            }
            objectNode.put(mappingName, value);
        } catch (IOException e) {
            LOG.info("Coding into JSON failed" + e.getMessage());
            return jsonString;
        }
        return objectNode.toString();
    }

    private String codeIntegerListIntoJsonString(String jsonString, String mappingName, List<Integer> integerList) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode;

        if (jsonString.isEmpty()){
            objectNode = new ObjectNode(JsonNodeFactory.instance);
        }
        else {
            try {
                objectNode = (ObjectNode) mapper.readTree(jsonString);
            } catch (IOException e) {
                LOG.info("Coding into JSON failed" + e.getMessage());
                return jsonString;
            }
        }
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for ( Integer integer : integerList) {
            arrayNode.add(integer);
        }

        objectNode.put(mappingName, arrayNode);
        return objectNode.toString();
    }
    public int getNoOfQuestionsToBePlayedFromJson(String json) {
        String noOfQuestionsToBePlayed = getFromJsonString(json, NO_OF_QUESTIONS_MAPPING_NAME);
        if (noOfQuestionsToBePlayed != null) {
            return Integer.parseInt(noOfQuestionsToBePlayed);
        } else {
            return 0;
        }

    }
}

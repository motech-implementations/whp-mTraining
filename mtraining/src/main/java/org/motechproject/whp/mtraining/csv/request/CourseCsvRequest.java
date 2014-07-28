package org.motechproject.whp.mtraining.csv.request;

import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.csv.domain.CallLogRecordType;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.split;

public class CourseCsvRequest {
    private String nodeName;
    private String nodeType;
    private CourseUnitState status;
    private String parentNode;
    private String description;
    private String fileName;
    private String options;
    private String correctAnswer;
    private String correctAnswerFileName;
    private String noOfQuizQuestions;
    private String passPercentage;

    public CourseCsvRequest() {
    }

    public CourseCsvRequest(String nodeName, String nodeType, CourseUnitState status, String parentNode, String description, String fileName) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.status = status;
        this.parentNode = parentNode;
        this.description = description;
        this.fileName = fileName;
    }

    public CourseCsvRequest(String nodeName, String nodeType, CourseUnitState status, String parentNode, String description,
                            String fileName, String options, String correctAnswer, String correctAnswerFileName, String noOfQuizQuestions, String passPercentage) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.status = status;
        this.parentNode = parentNode;
        this.description = description;
        this.fileName = fileName;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.correctAnswerFileName = correctAnswerFileName;
        this.noOfQuizQuestions = noOfQuizQuestions;
        this.passPercentage = passPercentage;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public CourseUnitState getStatus() {
        return status;
    }

    public String getParentNode() {
        return parentNode;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isCourse() {
        return CallLogRecordType.isCourse(nodeType);
    }

    public boolean hasParent() {
        return isNotBlank(parentNode);
    }

    public boolean isMessage() {
        return CallLogRecordType.isMessage(nodeType);
    }

    public boolean isQuestion() {
        return CallLogRecordType.isQuestion(nodeType);
    }

    public boolean isChapter() {
        return CallLogRecordType.isChapter(nodeType);
    }

    public boolean hasFileName() {
        return isNotBlank(fileName);
    }

    public String getOptions() {
        return options;
    }

    public boolean hasValidParentType(String parentNodeType) {
        CallLogRecordType callLogRecordType = CallLogRecordType.from(this.nodeType);
        return callLogRecordType.hasValidParentType(parentNodeType);
    }

    public String getChildNodeType() {
        CallLogRecordType childCallLogRecordType = CallLogRecordType.getChildNodeType(nodeType);
        return childCallLogRecordType != null ? childCallLogRecordType.name() : "";
    }

    public boolean isValidStatus() {
        return status == CourseUnitState.Pending || status == CourseUnitState.Active || status == CourseUnitState.Inactive;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCorrectAnswerFileName() {
        return correctAnswerFileName;
    }

    public String getNoOfQuizQuestions() {
        return noOfQuizQuestions;
    }

    public boolean isInActive() {
        return status == CourseUnitState.Inactive;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setStatus(CourseUnitState status) {
        this.status = status;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setCorrectAnswerFileName(String correctAnswerFileName) {
        this.correctAnswerFileName = correctAnswerFileName;
    }

    public void setNoOfQuizQuestions(String noOfQuizQuestions) {
        this.noOfQuizQuestions = noOfQuizQuestions;
    }

    public List<String> getOptionsAsList() {
        return isBlank(options) ? null : asList(split(options, ';'));
    }

    public String getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(String passPercentage) {
        this.passPercentage = passPercentage;
    }
}

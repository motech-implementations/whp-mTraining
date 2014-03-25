package org.motechproject.whp.mtraining.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;

import java.util.ArrayList;
import java.util.List;

public enum ContentType {
    COURSE {
        @Override
        public CourseDto toDto(String nodeName, String description, String fileName, boolean isActive, Integer numberOfQuizQuestions,
                               List<String> options, String correctAnswer, String correctAnswerFileName, Long passPercentage, List<Object> childDtos, String contentAuthor) {
            return new CourseDto(isActive, nodeName, description, contentAuthor, (List<ModuleDto>) (Object) childDtos);
        }
    },
    MODULE {
        @Override
        public ModuleDto toDto(String nodeName, String description, String fileName, boolean isActive, Integer numberOfQuizQuestions,
                               List<String> options, String correctAnswer, String correctAnswerFileName, Long passPercentage, List<Object> childDtos, String contentAuthor) {
            return new ModuleDto(isActive, nodeName, description, contentAuthor, (List<ChapterDto>) (Object) childDtos);
        }
    },
    CHAPTER {
        @Override
        public ChapterDto toDto(String nodeName, String description, String fileName, boolean isActive, Integer numberOfQuizQuestions,
                                List<String> options, String correctAnswer, String correctAnswerFileName, Long passPercentage, List<Object> childDtos, String contentAuthor) {
            List<QuestionDto> questions = filterChildNodesOfType(childDtos, QuestionDto.class);
            List<MessageDto> messages = filterChildNodesOfType(childDtos, MessageDto.class);
            QuizDto quizDto = new QuizDto(true, questions, numberOfQuizQuestions, passPercentage, contentAuthor);
            return numberOfQuizQuestions > 0 ? new ChapterDto(isActive, nodeName, description, contentAuthor, messages, quizDto) :
                    new ChapterDto(isActive, nodeName, description, contentAuthor, messages, null);
        }

        private <T> List<T> filterChildNodesOfType(List<Object> childDtos, Class<T> classType) {
            List<T> childrenOfGivenType = new ArrayList<>();
            for (Object childDto : childDtos) {
                if (childDto.getClass().equals(classType)) {
                    childrenOfGivenType.add((T) childDto);
                }
            }
            return childrenOfGivenType;
        }
    },
    MESSAGE {
        @Override
        public MessageDto toDto(String nodeName, String description, String fileName, boolean isActive, Integer numberOfQuizQuestions,
                                List<String> options, String correctAnswer, String correctAnswerFileName, Long passPercentage, List<Object> childDtos, String contentAuthor) {
            return new MessageDto(isActive, nodeName, fileName, description, contentAuthor);
        }
    },
    QUESTION {
        @Override
        public QuestionDto toDto(String nodeName, String description, String fileName, boolean isActive, Integer numberOfQuizQuestions,
                                 List<String> options, String correctAnswer, String correctAnswerFileName, Long passPercentage, List<Object> childDtos, String contentAuthor) {
            return new QuestionDto(isActive, nodeName, description, fileName, new AnswerDto(correctAnswer, correctAnswerFileName), options, contentAuthor);
        }
    };

    public static ContentType from(String nodeType) {
        return ContentType.valueOf(StringUtils.trimToEmpty(nodeType).toUpperCase());
    }

    public abstract Object toDto(String nodeName, String description, String fileName, boolean isActive, Integer numberOfQuizQuestions,
                                 List<String> options, String correctAnswer, String correctAnswerFileName, Long passPercentage, List<Object> childDtos, String contentAuthor);
}

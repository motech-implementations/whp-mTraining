package org.motechproject.whp.mtraining.domain;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.QuestionDto;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuestionTest {

    @Test
    public void shouldSetAnswerOptionsAsCommaSeparatedList() {
        QuestionDto questionDto = mock(QuestionDto.class);

        when(questionDto.getAnswer()).thenReturn(new AnswerDto("C", "answer-ext-id"));
        when(questionDto.getOptions()).thenReturn(Arrays.asList("A", "B", "C"));

        Question question = new Question(questionDto);

        assertThat(question.getAnswerOptions(), Is.is("A,B,C"));
    }

    @Test
    public void shouldSetAnswerOptionsAsNullWhenOptionsListIsBlank() {
        QuestionDto questionDto = mock(QuestionDto.class);

        when(questionDto.getAnswer()).thenReturn(new AnswerDto("C", "answer-ext-id"));
        when(questionDto.getOptions()).thenReturn(Collections.emptyList());

        Question question = new Question(questionDto);

        assertThat(question.getAnswerOptions(), IsNull.nullValue());
    }
}

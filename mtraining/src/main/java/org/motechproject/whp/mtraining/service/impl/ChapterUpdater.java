package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class ChapterUpdater extends Updater<ChapterDto> {

    private ChapterService chapterService;
    private MessageUpdater messageUpdater;
    private QuizUpdater quizUpdater;
    private List<ChapterDto> existingChapters;

    @Autowired
    public ChapterUpdater(ChapterService chapterService, MessageUpdater messageUpdater, QuizUpdater quizUpdater) {
        this.chapterService = chapterService;
        this.messageUpdater = messageUpdater;
        this.quizUpdater = quizUpdater;
        this.existingChapters = new ArrayList<>();
    }

    @Override
    protected void updateContentId(ChapterDto chapterDto, ChapterDto existingChapterDto) {
        chapterDto.setContentId(existingChapterDto.getContentId());
    }

    @Override
    protected void updateChildContents(ChapterDto chapterDto) {
        messageUpdater.update(chapterDto.getMessages());
        quizUpdater.update(newArrayList(chapterDto.getQuiz()));
    }

    @Override
    protected List<ChapterDto> getExistingContents() {
        if (existingChapters.isEmpty()) {
            existingChapters = chapterService.getAllChapters();
        }
        return existingChapters;
    }

    @Override
    protected boolean isEqual(ChapterDto chapterDto1, ChapterDto chapterDto2) {
        return chapterDto1.getName().equalsIgnoreCase(chapterDto2.getName());
    }

    public void invalidateCache() {
        existingChapters.clear();
        messageUpdater.invalidateCache();
        quizUpdater.invalidateCache();
    }
}

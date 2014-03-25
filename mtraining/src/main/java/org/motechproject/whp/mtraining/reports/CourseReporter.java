package org.motechproject.whp.mtraining.reports;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.domain.CertificationCourse;
import org.motechproject.whp.mtraining.domain.Chapter;
import org.motechproject.whp.mtraining.domain.Message;
import org.motechproject.whp.mtraining.domain.Module;
import org.motechproject.whp.mtraining.repository.AllCertificationCourses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CourseReporter {

    private final CourseService courseService;
    private final AllCertificationCourses allCertificationCourses;

    @Autowired
    public CourseReporter(CourseService courseService, AllCertificationCourses allCertificationCourses) {
        this.courseService = courseService;
        this.allCertificationCourses = allCertificationCourses;
    }

    public void reportCourseAdded(UUID courseId, Integer version) {
        CourseDto course = courseService.getCourse(new ContentIdentifierDto(courseId, version));
        CertificationCourse certificationCourse = map(course);
        allCertificationCourses.add(certificationCourse);
    }

    private CertificationCourse map(CourseDto courseDto) {
        List<Module> modules = new ArrayList<>();
        for (ModuleDto module : courseDto.getModules()) {
            modules.add(map(module));
        }
        return new CertificationCourse(courseDto.getName(), courseDto.getContentId(), courseDto.getVersion(), courseDto.getDescription(), courseDto.getCreatedBy(), courseDto.getCreatedOn(), modules);
    }

    private Module map(ModuleDto moduleDto) {
        List<Chapter> chapters = new ArrayList<>();
        for (ChapterDto chapterDto : moduleDto.getChapters()) {
            chapters.add(map(chapterDto));
        }
        return new Module(moduleDto.getName(), moduleDto.getContentId(), moduleDto.getVersion(), moduleDto.getDescription(), moduleDto.getCreatedBy(), moduleDto.getCreatedOn(), chapters);
    }

    private Chapter map(ChapterDto chapterDto) {
        List<Message> messages = new ArrayList<>();
        for (MessageDto messageDto : chapterDto.getMessages()) {
            messages.add(map(messageDto));
        }
        return new Chapter(chapterDto.getName(), chapterDto.getContentId(), chapterDto.getVersion(), chapterDto.getDescription(), chapterDto.getCreatedBy(), chapterDto.getCreatedOn(), messages);
    }

    private Message map(MessageDto messageDto) {
        return new Message(messageDto.getName(), messageDto.getContentId(), messageDto.getVersion(), messageDto.getDescription(), messageDto.getCreatedBy(), messageDto.getCreatedOn(), messageDto.getExternalId());
    }
}

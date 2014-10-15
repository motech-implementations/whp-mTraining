package org.motechproject.whp.mtraining.service;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.domain.ParentType;

import java.util.List;

public interface ManyToManyRelationService {

    ManyToManyRelation createRelation(ManyToManyRelation relation);

    ManyToManyRelation updateRelation(ManyToManyRelation relation);

    void deleteRelation(ManyToManyRelation relation);

    List<ManyToManyRelation> getAllRelations();

    List<ManyToManyRelation> getRelationsByChildId(long childId);

    ManyToManyRelation getRelationById(long id);

    List<ManyToManyRelation> getRelationsByParentType(ParentType parentType);

    void deleteRelationsByChildId(ParentType parentType, Long childId);

    void deleteRelationsById(Long id);

    void updateRelationsForCourse(List<ManyToManyRelation> relations, long courseId, List<Long> updatedIds);

    void updateRelationsForChild(List<ManyToManyRelation> relations, long childId);

    // CoursePlan - Course
    List<Course> getCoursesByParentId(long parentId);

    List<CoursePlan> getCoursePlansByChildId(long childId);

    // Course - Chapter
    List<Chapter> getChaptersByParentId(long parentId);

    List<Course> getCoursesByChildId(long childId);

    // Chapter - Lesson
    List<Lesson> getLessonsByParentId(long parentId);

    List<Chapter> getChaptersByChildId(long childId);

    // Chapter - Quiz
    Quiz getQuizByParentId(long parentId);

}

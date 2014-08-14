package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.repository.ManyToManyRelationDataService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationType;
import java.util.ArrayList;
import java.util.List;

@Service("manyToManyRelationService")
public class ManyToManyRelationServiceImpl implements ManyToManyRelationService {

    @Autowired
    private ManyToManyRelationDataService relationDataService;

    @Autowired
    private MTrainingService mTrainingService;

    @Autowired
    private CoursePlanService coursePlanService;

    @Override
    public ManyToManyRelation createRelation(ManyToManyRelation relation) {
        return relationDataService.create(relation);
    }

    @Override
    public ManyToManyRelation updateRelation(ManyToManyRelation relation) {
        return relationDataService.update(relation);
    }

    @Override
    public void deleteRelation(ManyToManyRelation relation) {
        relationDataService.delete(relation);
    }

    @Override
    public List<ManyToManyRelation> getAllRelations() {
        return relationDataService.retrieveAll();
    }

    @Override
    public ManyToManyRelation getRelationById(long id) {
        return relationDataService.findRelationById(id);
    }

    @Override
    public List<ManyToManyRelation> getRelationsByParentType(ParentType parentType) {
        return relationDataService.findRelations(parentType, null, null);
    }

    // CoursePlan - Course
    @Override
    public List<Course> getCoursesByParentId(long parentId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(ParentType.CoursePlan, parentId, null);
        List<Course> courses = new ArrayList<Course>();

        for(ManyToManyRelation relation : relations) {
            courses.add(mTrainingService.getCourseById(relation.getChildId()));
        }
        return courses;
    }

    @Override
    public List<CoursePlan> getCoursePlansByChildId(long childId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(ParentType.CoursePlan, null, childId);
        List<CoursePlan> coursePlans = new ArrayList<CoursePlan>();

        for(ManyToManyRelation relation : relations) {
            coursePlans.add(coursePlanService.getCoursePlanById(relation.getParentId()));
        }
        return coursePlans;
    }


    @Override
    public void deleteRelationsByChildId(ParentType parentType, Long childId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(parentType, null, childId);
        for(ManyToManyRelation relation : relations) {
            deleteRelation(relation);
        }
    }

    // Course - Chapter
    @Override
    public List<Chapter> getChaptersByParentId(long parentId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(ParentType.Course, parentId, null);
        List<Chapter> chapters = new ArrayList<Chapter>();

        for(ManyToManyRelation relation : relations) {
            chapters.add(mTrainingService.getChapterById(relation.getChildId()));
        }
        return chapters;
    }

    @Override
    public List<Course> getCoursesByChildId(long childId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(ParentType.Course, null, childId);
        List<Course> courses = new ArrayList<Course>();

        for(ManyToManyRelation relation : relations) {
            courses.add(mTrainingService.getCourseById(relation.getParentId()));
        }
        return courses;
    }

    // Chapter - Lesson
    @Override
    public List<Lesson> getLessonsByParentId(long parentId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(ParentType.Chapter, parentId, null);
        List<Lesson> lessons = new ArrayList<Lesson>();

        for(ManyToManyRelation relation : relations) {
            lessons.add(mTrainingService.getLessonById(relation.getChildId()));
        }
        return lessons;
    }

    @Override
    public List<Chapter> getChaptersByChildId(long childId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(ParentType.Chapter, null, childId);
        List<Chapter> chapters = new ArrayList<Chapter>();

        for(ManyToManyRelation relation : relations) {
            chapters.add(mTrainingService.getChapterById(relation.getParentId()));
        }
        return chapters;
    }

}

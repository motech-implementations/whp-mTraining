package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.repository.ManyToManyRelationDataService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

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

    @Autowired
    private DtoFactoryService dtoFactoryService;

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

    @Override
    public void deleteRelationsById(Long id) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(null, null, id);
        relations.addAll(relationDataService.findRelations(null, id, null));
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

    @Override
    public Quiz getQuizByParentId(long parentId) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(ParentType.Chapter, parentId, null);

        for(ManyToManyRelation relation : relations) {
            Quiz quiz = mTrainingService.getQuizById(relation.getChildId());
            if (quiz != null) {
                return quiz;
            }
        }
        return null;
    }

    @Override
    public void updateAll(final List<ManyToManyRelation> relations) {

        relationDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                relationDataService.deleteAll();
                for (ManyToManyRelation relation : relations) {
                    if(relationDataService.findRelations(relation.getParentType(), relation.getParentId(), relation.getChildId()).size() == 0) {
                        relationDataService.create(relation);
                    }
                }
            }
        });
    }

    private List<ManyToManyRelation> getRelationsForParent(long id) {
        List<ManyToManyRelation> relations = relationDataService.findRelations(null, id, null);
        for(int i = 0; i < relations.size(); i++) {
            if (relations.get(i).getParentType() != ParentType.Chapter) {
                relations.addAll(getRelationsForParent(relations.get(i).getChildId()));
            }
        }
        return relations;
    }

    @Override
    public void updateRelationsForCourse(final List<ManyToManyRelation> relations, long courseId) {
        boolean isEqual = true;
        List<ManyToManyRelation> existingRelations = getRelationsForParent(courseId);
        for (ManyToManyRelation relation : existingRelations) {
            if (!relations.contains(relation)) {
                relationDataService.delete(relation);
                isEqual = false;
            }
        }
        for(ManyToManyRelation relation : relations) {
            if (!existingRelations.contains(relation)) {
                relationDataService.create(relation);
                isEqual = false;
            }
        }
        if (!isEqual) {
            CoursePlanDto course = dtoFactoryService.getCourseDtoWithChildCollections(courseId);
            dtoFactoryService.updateCourseAndChildCollections(dtoFactoryService.increaseVersions(course));
        }
    }

}

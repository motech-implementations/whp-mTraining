package org.motechproject.whp.mtraining.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.whp.mtraining.domain.ProviderStatusDetailedReportQueryResult;
import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.ProviderStatusDetailedReportDto;
import org.motechproject.whp.mtraining.dto.ProviderWiseStatusReportDto;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.dto.TrainingStatusReportDto;
import org.motechproject.whp.mtraining.exception.DataExportException;
import org.motechproject.whp.mtraining.repository.CourseProgressDataService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ReportService;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.util.TableWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jdo.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, String> TRAINING_STATUS_REPORT_HEADER_MAP = new LinkedHashMap<String, String>() {
        {
            put("District",                 "district");
            put("Provider Registered",      "providerRegistered");
            put("Provider Completed Course","providerCompletedCourse");
            put("Provider In Course",       "providerInCourse");
        }
    };

    private static final Map<String, String> WISE_STATUS_REPORT_HEADER_MAP = new LinkedHashMap<String, String>() {
        {
            put("Provider Id",          "providerId");
            put("Training Start Date",  "trainingStartDate");
            put("Training End Date",    "trainingEndDate");
        }
    };

    private static final Map<String, String> STATUS_DETAILED_REPORT_HEADER_MAP = new LinkedHashMap<String, String>() {
        {
            put("Provider Id",                      "providerId");
            put("Training Start Date",              "trainingStartDate");
            put("Current Status",                   "currentStatus");
            put("Course Location",                  "courseLocation");
            put("Time Since In Current Location",   "timeSinceInCurrentLocation");
        }
    };

    private static final String TRAINING_STATUS_REPORT_QUERY = "select WHP_MTRAINING_LOCATION.district, count(WHP_MTRAINING_PROVIDER.location_id_OID) as 'providerRegistered', " +
            "SUM(if(WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'CLOSED' || WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'COMPLETED', 1, 0)) as 'providerCompletedCourse', " +
            "SUM(if(WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'STARTED' || WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'ONGOING', 1, 0)) as 'providerInCourse' " +
            "from WHP_MTRAINING_LOCATION left join WHP_MTRAINING_PROVIDER on WHP_MTRAINING_LOCATION.id = WHP_MTRAINING_PROVIDER.location_id_OID " +
            "left join WHP_MTRAINING_COURSEPROGRESS on WHP_MTRAINING_PROVIDER.callerId = WHP_MTRAINING_COURSEPROGRESS.callerId group by WHP_MTRAINING_LOCATION.district";

    private static final String STATUS_DETAILED_REPORT_QUERY = "select WHP_MTRAINING_COURSEPROGRESS.callerId as 'providerId', " +
            "WHP_MTRAINING_COURSEPROGRESS.courseStartTime as 'trainingStartDate', WHP_MTRAINING_COURSEPROGRESS.courseStatus as 'currentStatus', " +
            "WHP_MTRAINING_FLAG.dateModified as 'dateModified', courseIdentifier.contentId as 'coursePlanContentId', " +
            "moduleIdentifier.contentId as 'moduleContentId', chapterIdentifier.contentId as 'chapterContentId', " +
            "lessonIdentifier.contentId as 'lessonContentId', quizIdentifier.contentId as 'quizContentId' from WHP_MTRAINING_COURSEPROGRESS " +
            "left join WHP_MTRAINING_FLAG on WHP_MTRAINING_COURSEPROGRESS.flag_id_OID = WHP_MTRAINING_FLAG.id " +
            "left join WHP_MTRAINING_CONTENTIDENTIFIER as courseIdentifier on WHP_MTRAINING_FLAG.courseIdentifier_id_OID = courseIdentifier.id " +
            "left join WHP_MTRAINING_CONTENTIDENTIFIER as moduleIdentifier on WHP_MTRAINING_FLAG.moduleIdentifier_id_OID = moduleIdentifier.id " +
            "left join WHP_MTRAINING_CONTENTIDENTIFIER as chapterIdentifier on WHP_MTRAINING_FLAG.chapterIdentifier_id_OID = chapterIdentifier.id " +
            "left join WHP_MTRAINING_CONTENTIDENTIFIER as lessonIdentifier on WHP_MTRAINING_FLAG.lessonIdentifier_id_OID = lessonIdentifier.id " +
            "left join WHP_MTRAINING_CONTENTIDENTIFIER as quizIdentifier on WHP_MTRAINING_FLAG.quizIdentifier_id_OID = quizIdentifier.id";

    @Autowired
    private CourseProgressDataService courseProgressDataService;

    @Autowired
    private DtoFactoryService dtoFactoryService;

    @Override
    public List<TrainingStatusReportDto> getAllTrainingStatusReports() {
        return courseProgressDataService.executeQuery(new QueryExecution<List<TrainingStatusReportDto>>() {

            @Override
            public List<TrainingStatusReportDto> execute(Query query, InstanceSecurityRestriction restriction) {
                Query q = query.getPersistenceManager().newQuery("javax.jdo.query.SQL", TRAINING_STATUS_REPORT_QUERY);
                q.setResultClass(TrainingStatusReportDto.class);

                return (List<TrainingStatusReportDto>) q.execute();
            }
        });
    }

    @Override
    public List<ProviderWiseStatusReportDto> getAllWiseStatusReports() {
        List<CourseProgress> courseProgressList = courseProgressDataService.retrieveAll();
        List<ProviderWiseStatusReportDto> wiseStatusReportDtoList = new ArrayList<>();

        for (CourseProgress progress : courseProgressList) {
            DateTime trainingStartDate = ISODateTimeUtil.parseWithTimeZoneUTC(progress.getCourseStartTime());
            DateTime trainingEndDate = ISODateTimeUtil.parseWithTimeZoneUTC(progress.getCourseEndTime());

            wiseStatusReportDtoList.add(new ProviderWiseStatusReportDto(progress.getCallerId(), trainingStartDate, trainingEndDate));
        }

        return wiseStatusReportDtoList;
    }

    @Override
    public List<ProviderStatusDetailedReportDto> getAllStatusDetailedReports() {
        Map<String, CoursePlanDto> coursePlanDtoMap = new HashMap<>();
        Map<String, ModuleDto> moduleDtoMap = new HashMap<>();
        Map<String, ChapterDto> chapterDtoMap = new HashMap<>();
        Map<String, LessonDto> lessonDtoMap = new HashMap<>();
        Map<String, QuizDto> quizDtoMap = new HashMap<>();

        for (CoursePlanDto coursePlanDto : dtoFactoryService.getAllCoursePlanDtos()) {
            coursePlanDtoMap.put(coursePlanDto.getContentId().toString(), coursePlanDto);
        }
        for (ModuleDto moduleDto : dtoFactoryService.getAllModuleDtos()) {
            moduleDtoMap.put(moduleDto.getContentId().toString(), moduleDto);
        }
        for (ChapterDto chapterDto : dtoFactoryService.getAllChapterDtos()) {
            chapterDtoMap.put(chapterDto.getContentId().toString(), chapterDto);
        }
        for (LessonDto lessonDto : dtoFactoryService.getAllLessonDtos()) {
            lessonDtoMap.put(lessonDto.getContentId().toString(), lessonDto);
        }
        for (QuizDto quizDto : dtoFactoryService.getAllQuizDtos()) {
            quizDtoMap.put(quizDto.getContentId().toString(), quizDto);
        }

        List<ProviderStatusDetailedReportDto> statusDetailedReportDtoList = new ArrayList<>();
        DateTime now = DateTime.now();

        for (ProviderStatusDetailedReportQueryResult reportQueryResult : getProviderStatusDetailedReportQueryResults()) {
            String timeSinceInCurrentLocation = null;

            DateTime date = ISODateTimeUtil.parseWithTimeZoneUTC(reportQueryResult.getDateModified());
            if (date != null) {
                Period period = new Period(date, now);
                timeSinceInCurrentLocation = Days.daysBetween(date, now).getDays() + "d " + period.getHours() + "h";
            }

            CoursePlanDto coursePlanDto = coursePlanDtoMap.get(reportQueryResult.getCoursePlanContentId());
            ModuleDto moduleDto = moduleDtoMap.get(reportQueryResult.getModuleContentId());
            ChapterDto chapterDto = chapterDtoMap.get(reportQueryResult.getChapterContentId());
            LessonDto lessonDto = lessonDtoMap.get(reportQueryResult.getLessonContentId());
            QuizDto quizDto = quizDtoMap.get(reportQueryResult.getQuizContentId());

            String courseLocation = (coursePlanDto != null ? coursePlanDto.getName() + " - " : "") + (moduleDto != null ? moduleDto.getName() + " - " : "")
                    + (chapterDto != null ? chapterDto.getName() + " - " : "") + (lessonDto != null ? lessonDto.getName() : "") + (quizDto != null ? quizDto.getName() : "");

            DateTime trainingStartDate = ISODateTimeUtil.parseWithTimeZoneUTC(reportQueryResult.getTrainingStartDate());

            statusDetailedReportDtoList.add(new ProviderStatusDetailedReportDto(reportQueryResult.getProviderId(),
                    trainingStartDate, reportQueryResult.getCurrentStatus(), courseLocation, timeSinceInCurrentLocation));
        }

        return statusDetailedReportDtoList;
    }

    @Override
    public void exportTrainingStatusReport(TableWriter tableWriter) throws IOException {
        List<TrainingStatusReportDto> trainingStatusReportDtoList = getAllTrainingStatusReports();
        exportReport(tableWriter, TRAINING_STATUS_REPORT_HEADER_MAP, trainingStatusReportDtoList);
    }

    @Override
    public void exportProviderWiseStatusReport(TableWriter tableWriter) throws IOException {
        List<ProviderWiseStatusReportDto> wiseStatusReportDtoList = getAllWiseStatusReports();
        exportReport(tableWriter, WISE_STATUS_REPORT_HEADER_MAP, wiseStatusReportDtoList);
    }

    @Override
    public void exportProviderStatusDetailedReport(TableWriter tableWriter) throws IOException {
        List<ProviderStatusDetailedReportDto> statusDetailedReportDtoList = getAllStatusDetailedReports();
        exportReport(tableWriter, STATUS_DETAILED_REPORT_HEADER_MAP, statusDetailedReportDtoList);
    }

    private <T> void exportReport(TableWriter tableWriter, Map<String, String> headerMap, List<T> reportList) {
        Set<String> keys = headerMap.keySet();
        String[] headers = keys.toArray(new String[keys.size()]);
        try {
            tableWriter.writeHeader(headers);
            for (T report : reportList) {
                Map<String, String> row = buildRow(report, headerMap);
                tableWriter.writeRow(row, headers);
            }
        } catch (IOException e) {
            throw new DataExportException("Error when writing data", e);
        } finally {
            tableWriter.close();
        }
    }

    private <T> Map<String, String> buildRow(T entity, Map<String, String> headerMap) throws IOException {
        String json = OBJECT_MAPPER.writeValueAsString(entity);
        Map<String, Object> entityMap = OBJECT_MAPPER.readValue(json, new TypeReference<HashMap>(){});
        Map<String, String> row = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            String fieldName = entry.getValue();
            if (fieldName == null) {
                row.put(entry.getKey(), null);
                continue;
            }
            String[] fieldPath = fieldName.split("\\.");
            String value = null;
            if (fieldPath.length == 2) {
                Map<String, Object> objectMap = (Map<String, Object>) entityMap.get(fieldPath[0]);
                Object fieldValue = objectMap.get(fieldPath[1]);
                if (fieldValue != null) {
                    value = fieldValue.toString();
                }
            } else {
                Object entryValue = entityMap.get(entry.getValue());
                if (entryValue != null) {
                    value = entryValue.toString();
                }
            }
            row.put(entry.getKey(), value);
        }
        return  row;
    }

    private List<ProviderStatusDetailedReportQueryResult> getProviderStatusDetailedReportQueryResults() {
        return courseProgressDataService.executeQuery(new QueryExecution<List<ProviderStatusDetailedReportQueryResult>>() {

            @Override
            public List<ProviderStatusDetailedReportQueryResult> execute(Query query, InstanceSecurityRestriction restriction) {
                Query q = query.getPersistenceManager().newQuery("javax.jdo.query.SQL", STATUS_DETAILED_REPORT_QUERY);
                q.setResultClass(ProviderStatusDetailedReportQueryResult.class);

                return (List<ProviderStatusDetailedReportQueryResult>) q.execute();
            }
        });
    }
}

package com.quack.talk.api.report.repository;

import com.quack.talk.api.report.entity.Penalty;
import com.quack.talk.api.report.entity.Report;
import com.quack.talk.api.report.entity.ReportContents;
import com.quack.talk.api.report.mapper.ReportMapper;
import com.quack.talk.common.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ReportRepository {

    private final ReportMapper mapper;
    private final MongoTemplate mongoTemplate;
    private final MongoUtil mongoUtil;

    @Value("${mongodb.collection.report_contents}")
    private String COLLECTION_REPORT_CONTENTS;

    public ReportContents findContents(ReportContents.TargetType type) {
        Query query = mongoUtil.getQuery("type", type.toString());
        ReportContents reportContent = mongoTemplate.findOne(query, ReportContents.class, COLLECTION_REPORT_CONTENTS);
        return reportContent;
    }

    public int insertReport(Report report) {
        return mapper.insertReport(report);
    }

    public int countReport(String userId, String targetId) {
        return mapper.countReport(userId, targetId);
    }

    public int insertPenalty(Penalty penalty, int penaltyDays) {
        return mapper.insertPenalty(penalty, penaltyDays);
    }

    public Penalty findUnderPenaltyTarget(String targetId) {
        return mapper.findUnderPenaltyTarget(targetId);
    }
}

package com.quack.talk.api.report.mapper;

import com.quack.talk.api.report.entity.Penalty;
import com.quack.talk.api.report.entity.Report;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {

    int insertReport(Report report);

    int countReport(String userId, String targetId);

    int insertPenalty(Penalty penalty, int penaltyDays);

    Penalty findUnderPenaltyTarget(String targetId);
}

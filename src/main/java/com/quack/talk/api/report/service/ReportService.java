package com.quack.talk.api.report.service;

import com.quack.talk.api.cache.ReportCache;
import com.quack.talk.api.report.dto.ContentDTO;
import com.quack.talk.api.report.dto.ReportDTO;
import com.quack.talk.api.report.entity.*;
import com.quack.talk.api.report.repository.ReportRepository;
import com.quack.talk.api.util.mapper.QtalkMapper;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportCache reportCache;
    private final ReportRepository repository;
    private final QtalkMapper mapper;
    private final ChatUtil chatUtil;

    public List<ContentDTO> contents(ReportContents.TargetType type) {
        ReportContents reportContent = reportCache.findContents(type);
        Content[] contents = chatUtil.toArray(reportContent.getContents(), Content.class);
        return mapper.mapToReportContentDTO(contents);
    }

    public int insertReport(ReportDTO reportDTO, Friend myinfo) {
        setAdditionalReportData(reportDTO, myinfo);
        Report report = mapper.map(reportDTO);
        return repository.insertReport(report);
    }
    public int penalize(ReportDTO reportDTO) {
        // 신고 횟수 카운트
        int c = repository.countReport(reportDTO.getUserId(), reportDTO.getTargetId());
        if (c >= 3) {
            // 타겟이 유저냐 방이냐
            ReportContents.TargetType type = reportDTO.getType();
            int code = 0;
            int penaltyDays = 999999;
            switch (type){
                case USER:
                    code=401;
                    penaltyDays = 30;
                    break;
                case ROOM:
                    code=402;
                    break;
                default:
                    code=400;
                    break;
            }
            // 패널티 테이블에 데이터 추가
            Penalty penalty = Penalty.builder()
                    .code(code)
                    .targetId(reportDTO.getTargetId())
                    .type(type)
                    .build();
            return repository.insertPenalty(penalty, penaltyDays);
        }
        return 0;
    }

    private void setAdditionalReportData(ReportDTO reportDTO, Friend myinfo) {
        String reportCode = reportDTO.getCode();
        // 신고자 아이디
        reportDTO.setUserId(myinfo.getId());
        // 신고대상 타입
        ReportContents.TargetType type = getTargetTypeFromCode(reportCode);
        reportDTO.setType(type);
        // 신고내용
        ReportContents reportContent = reportCache.findContents(type);
        List<Content> contents = reportContent.getContents();
        String contentCode = getContentCodeFromCode(reportCode);
        Content content = contents.stream()
                .filter(c -> c.getCode().equals(contentCode))
                .findFirst().orElse(null);
        SubContent subContent = content.getSubContent().stream()
                .filter(sc -> sc.getCode().equals(reportCode))
                .findFirst().orElse(null);
        reportDTO.setContent(subContent.getContent());
    }
    private ReportContents.TargetType getTargetTypeFromCode(String code) {
        return code.startsWith("r") ? ReportContents.TargetType.ROOM : ReportContents.TargetType.USER;
    }
    private String getContentCodeFromCode(String code) {
        return code.substring(0,2);
    }


    public boolean isUnderPenalty(ReportDTO reportDTO) {
        Penalty penalty = repository.findUnderPenaltyTarget(reportDTO.getTargetId());
        return !ObjectUtils.isEmpty(penalty);
    }
}

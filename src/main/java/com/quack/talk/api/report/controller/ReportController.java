package com.quack.talk.api.report.controller;

import com.quack.talk.api.cache.UserCache;
import com.quack.talk.api.report.dto.ReportDTO;
import com.quack.talk.api.report.entity.ReportContents;
import com.quack.talk.api.report.service.ReportService;
import com.quack.talk.common.friend.entity.Friend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = {"/report"})
public class ReportController {

    private final UserCache userCache;
    private final ReportService reportService;

    @GetMapping("/contents")
    public ResponseEntity<?> contents(@RequestParam ReportContents.TargetType type){
        return ResponseEntity.ok(reportService.contents(type));
    }

    @PostMapping
    public ResponseEntity<?> report(@RequestHeader("Authorization") String accessToken,
                                    @RequestBody ReportDTO reportDTO){
        Friend myInfo = userCache.getMyinfo(accessToken);
        boolean isUnderPenalty = reportService.isUnderPenalty(reportDTO);
        // 패널티 없는 타겟만 신고
        if (!isUnderPenalty) {
            reportService.insertReport(reportDTO, myInfo);
            // TODO:: 비동기 or 배치로 변경
            int givePenalty = reportService.penalize(reportDTO);
            if (givePenalty > 0) {
                // TODO:: 유저 신고 - 모든 채팅방에서 강퇴, 방장일 경우 방장 변경
                // TODO:: 채팅방 신고 - 참여자들 나가기 처리
                // TODO:: 알림? 경고?
            }
        }
        return ResponseEntity.ok(null);
    }

}

package com.quack.talk.api.cache;

import com.quack.talk.api.report.entity.ReportContents;
import com.quack.talk.api.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportCache {

    private final ReportRepository repository;

    @Cacheable(value = "reportContent", key = "#type", unless = "#result == null")
    public ReportContents findContents(ReportContents.TargetType type) {
        return repository.findContents(type);
    }
}

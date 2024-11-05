package ru.t1.java.demo.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Override
    public List<DataSourceErrorLog> getAllErrorLogs() {
        return dataSourceErrorLogRepository.findAll();
    }

    @Override
    public DataSourceErrorLog getErrorLogById(Long id) {
        return dataSourceErrorLogRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Error log not found"));
    }

    @Override
    public DataSourceErrorLog createErrorLog(DataSourceErrorLog errorLog) {
        return dataSourceErrorLogRepository.save(errorLog);
    }
}
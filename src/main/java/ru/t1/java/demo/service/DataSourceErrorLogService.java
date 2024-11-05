package ru.t1.java.demo.service;

import ru.t1.java.demo.model.DataSourceErrorLog;

import java.util.List;

public interface DataSourceErrorLogService {

    List<DataSourceErrorLog> getAllErrorLogs();

    DataSourceErrorLog getErrorLogById(Long id);

    DataSourceErrorLog createErrorLog(DataSourceErrorLog errorLog);
}

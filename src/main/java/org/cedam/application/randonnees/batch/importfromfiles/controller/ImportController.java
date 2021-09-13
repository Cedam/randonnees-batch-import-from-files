package org.cedam.application.randonnees.batch.importfromfiles.controller;

import lombok.extern.log4j.Log4j2;
import org.cedam.application.randonnees.batch.importfromfiles.BatchLauncher;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load")
@Log4j2
public class ImportController {

    @Autowired
    private BatchLauncher batchLauncher;

    @GetMapping
    public BatchStatus load() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        log.info("Batch démarré à la demande");
        return batchLauncher.run();
    }
}

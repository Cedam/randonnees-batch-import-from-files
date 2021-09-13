package org.cedam.application.randonnees.batch.importfromfiles;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Execution du batch via la crontable
 */
@Component
@Log4j2
public class Scheduler {

    @Autowired
    private BatchLauncher batchLauncher;

    @Scheduled(cron="${batch.cron}")
    public void perform() throws Exception {
        log.info("Batch programmé");
        batchLauncher.run();
    }
}

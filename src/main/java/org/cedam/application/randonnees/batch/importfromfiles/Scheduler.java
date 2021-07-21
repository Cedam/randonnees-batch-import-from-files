package org.cedam.application.randonnees.batch.importfromfiles;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class Scheduler {

    @Autowired
    private BatchLauncher batchLauncher;

    //@Scheduled(fixedDelayString = "${batch.cron}")
    @Scheduled(cron="${batch.cron}")
    public void perform() throws Exception {
        log.info("Batch programmé");
        batchLauncher.run();
    }
}

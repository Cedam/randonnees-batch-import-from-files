package org.cedam.application.randonnees.batch.importfromfiles;

import lombok.extern.log4j.Log4j2;
import org.cedam.application.randonnees.batch.importfromfiles.Constantes;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultat de l'execution du batch
 */
@Component
@Log4j2
public class JobListener extends JobExecutionListenerSupport {


	@Autowired
	public JobListener() {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		List<String> fileIntegrate = (ArrayList<String>) jobExecution.getExecutionContext().get(Constantes.CONTEXT_RESOURCES_TREK);
		fileIntegrate.addAll((ArrayList<String>) jobExecution.getExecutionContext().get(Constantes.CONTEXT_RESOURCES_DAY));
		for (String filePath : fileIntegrate) {
			File file = new File(filePath);
			File newFile = new File(file.getPath() + ".old");
			boolean move = file.renameTo(newFile);
			log.info(String.format("Rename %s to %s", file.getPath(), newFile.getName()));
			if (!move) {
				throw new UnexpectedJobExecutionException("Could not move file " + file.getPath());
			}
		}

		String dateStr = Constantes.dateFormat.format(jobExecution.getStartTime());
		log.info(String.format("JOB %d (start at %s) FINISHED, status => %s ", jobExecution.getJobId(), dateStr, jobExecution.getStatus()));

	}
}

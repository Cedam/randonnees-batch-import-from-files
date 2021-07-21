package org.cedam.application.randonnees.batch.importfromfiles;

import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.cedam.application.randonnees.batch.importfromfiles.step.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resources;
import javax.sql.DataSource;
import java.io.File;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {

    private static final String JOB_NAME = "importJob";
    private static final String STEP_NAME = "step1";

    @Value("${reader.file.name}")
    private String fileNameReader;

    @Value("${reader.path.name}")
    private String pathNameReader;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public Step step1(RestServiceWriter<TrekDto> writer) {
        return stepBuilderFactory.get(STEP_NAME).transactionManager(transactionManager)
                .<TrekDto, TrekDto>chunk(2)//Execution du batch par lot de 2
                .reader(batchReaderMultiFiles())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public FileReader batchReaderFile() {
        return new FileReader(fileNameReader);
    }


    @Bean
    public MultiFilesReader batchReaderMultiFiles() {
        return new MultiFilesReader(pathNameReader);
    }

    @Bean
    public TrekItemProcessor processor() {
        return new TrekItemProcessor();
    }

    @Bean
    public RestServiceWriter writer() {
        return new RestServiceWriter();
    }

    @Bean
    public Job importUserJob(JobListener listener) throws Exception {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .repository(jobRepositoryObj())
                .start(step1(writer()))
                .next(step2())
                .build();
    }

    public void Rien() {
        String rien = "";
    }

    @Bean
    public Step step2() {
        FileMovingJob task = new FileMovingJob();
        //task.setResources(new Resource[] { new ClassPathResource(fileNameReader) });
        task.setResources(MultiFilesReader.getInputResources(pathNameReader));
        return stepBuilderFactory.get("step2")
                .tasklet(task)
                .build();
    }

    @Bean
    public JobRepository jobRepositoryObj() throws Exception {
        JobRepositoryFactoryBean jobRepoFactory = new JobRepositoryFactoryBean();
        jobRepoFactory.setTransactionManager(transactionManager);
        jobRepoFactory.setDataSource(dataSource);
        return jobRepoFactory.getObject();
    }

}

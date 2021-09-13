package org.cedam.application.randonnees.batch.importfromfiles;

import org.cedam.application.randonnees.batch.importfromfiles.dto.DayDto;
import org.cedam.application.randonnees.batch.importfromfiles.dto.LineDayDto;
import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.cedam.application.randonnees.batch.importfromfiles.stepDay.DayItemProcessor;
import org.cedam.application.randonnees.batch.importfromfiles.stepDay.DayMultiFilesReader;
import org.cedam.application.randonnees.batch.importfromfiles.stepDay.DayRestServiceWriter;
import org.cedam.application.randonnees.batch.importfromfiles.stepTrek.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {

    private static final String JOB_NAME = "importJob";
    private static final String STEP1_NAME = "step1";
    private static final String STEP2_NAME = "step2";

    @Value("${reader.path.name}")
    private String pathNameReader;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;



    @Bean
    public JobRepository jobRepositoryObj() throws Exception {
        JobRepositoryFactoryBean jobRepoFactory = new JobRepositoryFactoryBean();
        jobRepoFactory.setTransactionManager(transactionManager);
        jobRepoFactory.setDataSource(dataSource);
        return jobRepoFactory.getObject();
    }


    @Bean
    public Job importUserJob(JobListener listener) throws Exception {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .repository(jobRepositoryObj())
                .start(step1(writer()))
                .next(step2(writerDay()))
                .build();
    }



    @Bean
    public TrekMultiFilesReader batchReaderMultiFilesTrek() {
        return new TrekMultiFilesReader(pathNameReader);
    }

    @Bean
    public TrekItemProcessor processor() {
        return new TrekItemProcessor();
    }

    @Bean
    public TrekRestServiceWriter writer() {
        return new TrekRestServiceWriter();
    }

    @Bean
    public Step step1(TrekRestServiceWriter<TrekDto> writer) {
        return stepBuilderFactory.get(STEP1_NAME).transactionManager(transactionManager)
                .<TrekDto, TrekDto>chunk(2) //Execution du batch par lot de 2
                .reader(batchReaderMultiFilesTrek())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public DayMultiFilesReader batchReaderMultiFilesDay() {
        return new DayMultiFilesReader(pathNameReader);
    }

    @Bean
    public DayItemProcessor processorDay() {
        return new DayItemProcessor();
    }

    @Bean
    public DayRestServiceWriter writerDay() {
        return new DayRestServiceWriter();
    }

    @Bean
    public Step step2(DayRestServiceWriter<DayDto> writerDay) {
        return stepBuilderFactory.get(STEP2_NAME).transactionManager(transactionManager)
                .<LineDayDto, DayDto>chunk(2) //Execution du batch par lot de 2
                .reader(batchReaderMultiFilesDay())
                .processor(processorDay())
                .writer(writerDay)
                .build();
    }

}

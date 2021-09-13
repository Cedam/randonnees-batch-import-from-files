package org.cedam.application.randonnees.batch.importfromfiles.stepDay;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.cedam.application.randonnees.batch.importfromfiles.Constantes;
import org.cedam.application.randonnees.batch.importfromfiles.dto.DayDto;
import org.cedam.application.randonnees.batch.importfromfiles.dto.LineDayDto;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Liste les fichiers a traiter
 * Pour chaque fichier :
 *      -Pour chaque ligne => convertie la ligne en LineDayDto
 */
@Log4j2
public class DayMultiFilesReader extends MultiResourceItemReader<LineDayDto> {

    private static final String FILTER_PREFIX = "day";
    private static final String FILTER_EXTENSION = "csv";
    private static final String READER_NAME = "itemReader";
    private final String workDirPath;
    private FileSystemResource[] inputResources;

    public DayMultiFilesReader(String workDirPath) {
        this.workDirPath = workDirPath;
        log.info("Starting to read input from : " + workDirPath);

        this.setResources(getInputResources(workDirPath));
        this.setDelegate(readOneFile());
    }

    @Override
    public void open(ExecutionContext executionContext) {
        Assert.notNull(workDirPath, "workDirPath must be set");
        inputResources = getInputResources(workDirPath);
        super.setResources(inputResources);
        super.open(executionContext);
    }


    /**
     *
     * @param workDirPath
     * @return
     */
    public static FileSystemResource[] getInputResources(String workDirPath) {

        List<FileSystemResource> inputResources = new ArrayList<>();
        File inputDir = new File(workDirPath);

        if(inputDir != null && inputDir.isDirectory()) {
            File[] inputFiles = inputDir.listFiles();
            //Filtre fichier CSV
            var filteredInputFiles = Arrays.stream(inputFiles).filter(x -> FilenameUtils.getExtension(x.getName()).equals(FILTER_EXTENSION)).collect(Collectors.toList());
            //Filtre fichier TREK
            filteredInputFiles = filteredInputFiles.stream().filter(x -> x.getName().startsWith(FILTER_PREFIX)).collect(Collectors.toList());
            if(inputFiles != null) {
                for (File file : filteredInputFiles) {
                    log.info("Reading file : " + file.getAbsolutePath());
                    FileSystemResource resource = new FileSystemResource(file);
                    inputResources.add(resource);
                }
            }
        }
        return inputResources.toArray(new FileSystemResource[inputResources.size()]);
    }

    @AfterStep
    public void afterStepUpdateContext(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        jobContext.put(Constantes.CONTEXT_RESOURCES_DAY, Arrays.stream(inputResources).map(x -> x.getPath()).collect(Collectors.toList()));
    }

    /**
     *
     * @return
     */
    private FlatFileItemReader<LineDayDto> readOneFile() {

        FlatFileItemReader<LineDayDto> resourceReader = new FlatFileItemReader<>();

        //skip the first line which is the file header
        //resourceReader.setLinesToSkip(1);

        resourceReader.setLineMapper(new DefaultLineMapper<>() {

            private final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(";");
            private final FieldSetMapper<LineDayDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>() {
                @Override
                public LineDayDto mapFieldSet(FieldSet pFielSet) throws BindException {
                    LineDayDto inputData = new LineDayDto();
                    inputData.setNumber(pFielSet.readString("number"));
                    inputData.setTrekName(pFielSet.readString("trekName"));
                    return inputData;
                }
            };

            @Override
            public LineDayDto mapLine(String pLine, int pLineNumber) throws Exception {
                lineTokenizer.setNames("number", "trekName");
                lineTokenizer.setStrict(false); //Fichier d'entree obligatoire'
                return fieldSetMapper.mapFieldSet(lineTokenizer.tokenize(pLine));
            }

        });

        return resourceReader;
    }


}

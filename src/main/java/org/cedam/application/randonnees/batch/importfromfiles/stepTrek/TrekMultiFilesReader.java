package org.cedam.application.randonnees.batch.importfromfiles.stepTrek;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.cedam.application.randonnees.batch.importfromfiles.Constantes;
import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
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
 *      -Pour chaque ligne => convertie la ligne en TrekDto
 */
@Log4j2
public class TrekMultiFilesReader extends MultiResourceItemReader<TrekDto> {

    private static final String FILTER_PREFIX = "trek";
    private static final String FILTER_EXTENSION = "csv";
    private static final String READER_NAME = "itemReader";
    private final String workDirPath;
    private FileSystemResource[] inputResources;

    public TrekMultiFilesReader(String workDirPath) {
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
        jobContext.put(Constantes.CONTEXT_RESOURCES_TREK, Arrays.stream(inputResources).map(x -> x.getPath()).collect(Collectors.toList()));
    }

    /**
     *
     * @return
     */
    private FlatFileItemReader<TrekDto> readOneFile() {

        FlatFileItemReader<TrekDto> resourceReader = new FlatFileItemReader<>();

        //skip the first line which is the file header
        //resourceReader.setLinesToSkip(1);

        resourceReader.setLineMapper(new DefaultLineMapper<>() {

            private final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(";");
            private final FieldSetMapper<TrekDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>() {
                @Override
                public TrekDto mapFieldSet(FieldSet pFielSet) throws BindException {
                    TrekDto inputData = new TrekDto();
                    inputData.setName(pFielSet.readString("name"));
                    inputData.setYear(Integer.parseInt(pFielSet.readString("year")));
                    inputData.setLocation(pFielSet.readString("location"));
                    return inputData;
                }
            };

            @Override
            public TrekDto mapLine(String pLine, int pLineNumber) throws Exception {
                lineTokenizer.setNames("name", "year", "location");
                lineTokenizer.setStrict(false); //Fichier d'entree obligatoire'
                return fieldSetMapper.mapFieldSet(lineTokenizer.tokenize(pLine));
            }

        });

        return resourceReader;
    }


}

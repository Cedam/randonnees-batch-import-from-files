package org.cedam.application.randonnees.batch.importfromfiles.step;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class MultiFilesReader extends MultiResourceItemReader<TrekDto> {

    private static final String READER_NAME = "itemReader";
    private String workDirPath;

    public MultiFilesReader(String workDirPath) {
        this.workDirPath = workDirPath;
        log.info("Starting to read input from : " + workDirPath);
        //setLineMapper(lineMapperTrek());

        this.setResources(getInputResources(workDirPath));
        this.setDelegate(readOneFile());
    }

    @Override
    public void open(ExecutionContext executionContext) {
        Assert.notNull(workDirPath, "workDirPath must be set");
        super.setResources(getInputResources(workDirPath));
        super.open(executionContext);
    }

    /**
     * Get all the resources (files) to be readen by this spring batch reader.
     *
     * @param workDirPath
     * @return
     */
    public static FileSystemResource[] getInputResources(String workDirPath) {

        List<FileSystemResource> inputResources = new ArrayList<FileSystemResource>();
        File inputDir = new File(workDirPath);

        if(inputDir != null && inputDir.isDirectory()) {
            File[] inputFiles = inputDir.listFiles();
            var filteredInputFiles = Arrays.stream(inputFiles).filter(x -> FilenameUtils.getExtension(x.getName()).equals("csv")).collect(Collectors.toList());
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


    /**
     * Set and return a FlatFileItemReader to read one resource.
     *
     * @return
     */
    private FlatFileItemReader<TrekDto> readOneFile() {

        FlatFileItemReader<TrekDto> resourceReader = new FlatFileItemReader<TrekDto>();

        //skip the first line which is the file header
        //resourceReader.setLinesToSkip(1);

        resourceReader.setLineMapper(new DefaultLineMapper<TrekDto>() {

            private DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(";");
            private FieldSetMapper<TrekDto> fieldSetMapper = new BeanWrapperFieldSetMapper<TrekDto>() {
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
                lineTokenizer.setNames(new String[]{"name", "year", "location"});
                lineTokenizer.setStrict(false); //Fichier d'entree obligatoire'
                return fieldSetMapper.mapFieldSet(lineTokenizer.tokenize(pLine));
            }

        });

        return resourceReader;
    }


}

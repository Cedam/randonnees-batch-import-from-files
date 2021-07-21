package org.cedam.application.randonnees.batch.importfromfiles.step;

import lombok.extern.log4j.Log4j2;
import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;

@Log4j2
public class FileReader extends FlatFileItemReader<TrekDto> {

    private static final String READER_NAME = "itemReader";

    public FileReader(String fileName) {
        log.info("Starting to read input from : " + fileName);
        setName(READER_NAME);
        setResource(new ClassPathResource(fileName));
        setLineMapper(lineMapperTrek());
    }

    private LineMapper<TrekDto> lineMapperTrek()
    {
         var defaultLineMapper = new DefaultLineMapper<TrekDto>() {};

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer()
        {{
            setNames("name", "year", "location");
            setDelimiter(";");
            setStrict(true);
        }};

        BeanWrapperFieldSetMapper<TrekDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>() {{
            setTargetType(TrekDto.class);
        }};

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }


}

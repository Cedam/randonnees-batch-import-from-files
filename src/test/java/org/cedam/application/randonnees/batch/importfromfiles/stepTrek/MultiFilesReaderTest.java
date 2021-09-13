package org.cedam.application.randonnees.batch.importfromfiles.stepTrek;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
class MultiFilesReaderTest {

    private final String FILE_NAME = "trek-sample-data1.csv";

    @Autowired
    private TrekMultiFilesReader object;

    @Test
    void testContextLoads() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        var list = TrekMultiFilesReader.getInputResources("src/test/resources/importFiles");
        assertThat(2).isEqualTo(list.length);
        assertThat(FILE_NAME).isEqualTo(list[0].getFilename());
    }


//    private String GetInfosFile(String pathFile)
//    {
//        StringBuilder result = new StringBuilder("TEST : "+ pathFile+"\r\n");
//
//        try {
//            File file = new File(pathFile);
//            result.append(" Exists : " + file.exists()).append("\r\n");
//            result.append(" GetPath : " + file.getPath()).append("\r\n");
//            result.append(" GetAbsolutePath : " + file.getAbsolutePath()).append("\r\n");
//            result.append(" GetParentFile : " + file.getParentFile()).append("\r\n");
//        }
//        catch(Exception ex)
//        {
//            result.append("ERROR : "+ex.getMessage());
//        }
//        return result.toString();
//    }

}

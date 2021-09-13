package org.cedam.application.randonnees.batch.importfromfiles.stepTrek;

import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TrekItemProcessorTest {

    @Autowired
    private TrekItemProcessor object;

    @Test
    void testContextLoads() throws Exception {
        TrekDto trekDtoIn = new TrekDto();
        trekDtoIn.setId((long) Math.random()* Integer.MAX_VALUE);
        trekDtoIn.setName("Name1");
        trekDtoIn.setYear(2021);
        TrekDto trekDtoOut = object.process(trekDtoIn);
        assertThat(trekDtoIn.toString()).isEqualTo(trekDtoOut.toString());
    }


}

package org.cedam.application.randonnees.batch.importfromfiles.stepDay;

import feign.RetryableException;
import lombok.extern.log4j.Log4j2;
import org.cedam.application.randonnees.batch.importfromfiles.proxies.DayServiceProxy;
import org.cedam.application.randonnees.batch.importfromfiles.proxies.TrekServiceProxy;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * Traite un element DayDto, c'est a dire fait l'insertion en BDD
 * @param <DayDto>
 */
@Log4j2
public class DayRestServiceWriter<DayDto> implements ItemWriter {


    @Autowired
    private DayServiceProxy dayServiceProxy;

    @Override
    public void write(List list) throws Exception {
        try
        {
            list.forEach(day -> {
                log.info("Save " + day.toString());
                dayServiceProxy.save((org.cedam.application.randonnees.batch.importfromfiles.dto.DayDto) day);
            });
        }
        catch(RetryableException ex) {
            if (ex.getCause() instanceof java.net.ConnectException) {
                log.error("Impossible de se connecter au service : " + ex.request().url());
                throw ex;
            }
            throw ex;
        }




    }

}

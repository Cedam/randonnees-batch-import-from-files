package org.cedam.application.randonnees.batch.importfromfiles.stepTrek;

import feign.RetryableException;
import lombok.extern.log4j.Log4j2;
import org.cedam.application.randonnees.batch.importfromfiles.proxies.TrekServiceProxy;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * Traite un element TrekDto, c'est a dire fait l'insertion en BDD
 * @param <TrekDto>
 */
@Log4j2
public class TrekRestServiceWriter<TrekDto> implements ItemWriter {


    @Autowired
    private TrekServiceProxy trekServiceProxy;

    @Override
    public void write(List list) throws Exception {
        try
        {
            list.forEach(trek -> {
                log.info("Save " + trek.toString());
                trekServiceProxy.save((org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto) trek);
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

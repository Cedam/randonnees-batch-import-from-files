package org.cedam.application.randonnees.batch.importfromfiles.step;

import feign.RetryableException;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.cedam.application.randonnees.batch.importfromfiles.proxies.TrekServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Log4j2
public class RestServiceWriter<TrekDto> implements ItemWriter {


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

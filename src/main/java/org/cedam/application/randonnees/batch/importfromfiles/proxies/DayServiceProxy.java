package org.cedam.application.randonnees.batch.importfromfiles.proxies;

import org.cedam.application.randonnees.batch.importfromfiles.dto.DayDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "randonnees-back-day", url = "${writer.ws.url}", path = "/days")
public interface DayServiceProxy {

    @GetMapping(value = "/{id}")
    DayDto getById(@PathVariable("id") long id);

    @PostMapping(value = "/save")
    DayDto save(@RequestBody DayDto day);
    
}

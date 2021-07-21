package org.cedam.application.randonnees.batch.importfromfiles.proxies;

import java.util.List;

import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "randonnees-back-trek", url = "${writer.ws.url}", path = "/treks")
public interface TrekServiceProxy {


    @GetMapping(value = "")
    List<TrekDto> getAll();

    @GetMapping(value = "/{id}")
    TrekDto getById(@PathVariable("id") long id);

    @PostMapping(value = "/save")
    TrekDto save(@RequestBody TrekDto trek);

}

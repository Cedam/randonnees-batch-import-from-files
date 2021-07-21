package org.cedam.application.randonnees.batch.importfromfiles.step;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

@Log4j2
public class TrekItemProcessor implements ItemProcessor<TrekDto, TrekDto> {

	@Override
	public TrekDto process(final TrekDto person) throws Exception {

		final TrekDto transformedPerson = new TrekDto();
		transformedPerson.setId(person.getId());
		transformedPerson.setName(person.getName());
		transformedPerson.setYear(person.getYear());
		transformedPerson.setLocation(person.getLocation());

		log.info("Converting (" + person + ") into (" + transformedPerson + ")");

		return transformedPerson;
	}

}

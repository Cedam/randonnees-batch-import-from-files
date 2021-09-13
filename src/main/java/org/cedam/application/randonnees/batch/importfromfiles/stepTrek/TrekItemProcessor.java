package org.cedam.application.randonnees.batch.importfromfiles.stepTrek;

import lombok.extern.log4j.Log4j2;
import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;

import org.springframework.batch.item.ItemProcessor;

/**
 * Ne sert a rien dans notre cas :
 * Transforme un element TrekDto en TrekDto
 */
@Log4j2
public class TrekItemProcessor implements ItemProcessor<TrekDto, TrekDto> {

	@Override
	public TrekDto process(final TrekDto trekDtoIn) throws Exception {

		final TrekDto transformedTrekDto = new TrekDto();
		transformedTrekDto.setName(trekDtoIn.getName());
		transformedTrekDto.setYear(trekDtoIn.getYear());
		transformedTrekDto.setLocation(trekDtoIn.getLocation());

		log.info("Converting (" + trekDtoIn + ") into (" + transformedTrekDto + ")");

		return transformedTrekDto;
	}

}

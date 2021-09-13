package org.cedam.application.randonnees.batch.importfromfiles.stepDay;

import lombok.extern.log4j.Log4j2;
import org.cedam.application.randonnees.batch.importfromfiles.dto.DayDto;
import org.cedam.application.randonnees.batch.importfromfiles.dto.LineDayDto;

import org.cedam.application.randonnees.batch.importfromfiles.dto.TrekDto;
import org.cedam.application.randonnees.batch.importfromfiles.proxies.DayServiceProxy;
import org.cedam.application.randonnees.batch.importfromfiles.proxies.TrekServiceProxy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Ne sert a rien dans notre cas :
 * Transforme un element LineDayDto en DayDto
 */
@Log4j2
public class DayItemProcessor implements ItemProcessor<LineDayDto, DayDto> {

	@Autowired
	private TrekServiceProxy trekServiceProxy;

	@Override
	public DayDto process(final LineDayDto lineDayDtoIn) throws Exception {

		final DayDto transformedDayDto = new DayDto();
		transformedDayDto.setNumber(lineDayDtoIn.getNumber());
		TrekDto trekDto = trekServiceProxy.getByName(lineDayDtoIn.getTrekName());
		transformedDayDto.setTrek(trekDto);
		//transformedDayDto.setYear(dayDtoIn.getYear());

		log.info("Converting (" + lineDayDtoIn + ") into (" + transformedDayDto + ")");

		return transformedDayDto;
	}

}

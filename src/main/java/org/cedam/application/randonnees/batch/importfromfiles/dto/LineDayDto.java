package org.cedam.application.randonnees.batch.importfromfiles.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@ToString
public class LineDayDto {

	private String number;

	private String trekName;

	@Override
	public String toString() {
		return String.format("%s %s", number, trekName);
	}

}

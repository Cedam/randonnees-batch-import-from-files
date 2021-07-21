package org.cedam.application.randonnees.batch.importfromfiles.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Service
@Getter
@Setter
@ToString
public class TrekDto {

	private long id;

	private String name;

	private int year;

	private String location;

	private List days = new ArrayList<DayDto>();

}

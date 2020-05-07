package org.twittercity.twitterdataminer.database;

import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;

public class QueryFiltersConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> list) {
		return String.join(",",list);
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		return Arrays.asList(dbData.split(","));
	}

}

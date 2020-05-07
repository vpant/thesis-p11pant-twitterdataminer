package org.twittercity.twitterdataminer.database;

import javax.persistence.AttributeConverter;

import org.twittercity.twitterdataminer.twitter.models.Feeling;

public class FeelingEnumConverter implements AttributeConverter<Feeling, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Feeling attribute) {
		return new Integer(attribute.getFeelingID());
	}

	@Override
	public Feeling convertToEntityAttribute(Integer dbData) {
		return Feeling.forFeelingID(dbData);
	}

}

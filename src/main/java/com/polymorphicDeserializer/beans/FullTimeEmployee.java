package com.polymorphicDeserializer.beans;

import com.polymorphicDeserializer.annotations.PropertyMatch;
import com.polymorphicDeserializer.annotations.Subtype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true, includeFieldNames = true)
@Subtype // <-------- registers as subType of Employee to deserialize in runtime
public class FullTimeEmployee extends Employee {
	/**
	 * if request object contains {@code @PropertyMatch } annotated field attribute, then it will deserialize to {@code FullTimeEmployee}
	 */
	@PropertyMatch
	private double salary;

}
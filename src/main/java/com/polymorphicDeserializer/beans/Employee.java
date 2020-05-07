package com.polymorphicDeserializer.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@ToString(callSuper=true, includeFieldNames=true)
public class Employee {
    private Long employeeId;
    private String name;
    
}
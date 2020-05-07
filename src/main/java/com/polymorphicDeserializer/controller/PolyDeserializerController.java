package com.polymorphicDeserializer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.polymorphicDeserializer.beans.Employee;
import com.polymorphicDeserializer.beans.FullTimeEmployee;
import com.polymorphicDeserializer.beans.PartTimeEmployee;

/**
 * 
 * @author <a href="mailto:rohith.rohith009@gmail.com">Rohith Varala</a>
 *
 * 
 */
@RestController
@RequestMapping
public class PolyDeserializerController {
	private final static Logger LOG = LoggerFactory.getLogger(PolyDeserializerController.class);

	/**
	 * here, we receive request with {@code Employee} or its subTypes
	 * {@code FullTimeEmployee} or {@code PartTimeEmployee}
	 * 
	 * 
	 * @param employee
	 * @return
	 * 
	 * @see com.polymorphicDeserializer.beans.FullTimeEmployee
	 * @see com.polymorphicDeserializer.beans.PartTimeEmployee
	 */
	@RequestMapping(value = "/saveEmployee", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public Employee saveEmployee(@RequestBody Employee employee) {
		LOG.info("Received request :" + employee);

		return employee;
	}

}
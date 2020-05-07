package com.polymorphicDeserializer.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.polymorphicDeserializer.annotations.PropertyMatch;
import com.polymorphicDeserializer.annotations.Subtype;

/**
 * This class is responsible to register custom deserializable types
 * on Application startup by introspection of {@code Subtype}, {@code PropertyMatch}
 * custom annotations on top of class & field respectively.
 * 
 * @author <a href="mailto:rohith.rohith009@gmail.com">Rohith Varala</a>
 * 
 * @see com.polymorphicDeserializer.config.GenericPolymorphicDeserializer
 * @see com.polymorphicDeserializer.annotations.Subtype
 * @see com.polymorphicDeserializer.annotations.PropertyMatch
 */
@Configuration
public class DeserializerRegistry {
	private final static Logger LOG = LoggerFactory.getLogger(DeserializerRegistry.class);

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	public void register() {
		try {
			Map<Class<?>, Map<String, String>> map = new HashMap<Class<?>, Map<String, String>>();
			ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
					false);
			provider.addIncludeFilter(new AnnotationTypeFilter(Subtype.class));
			Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents("com"); // package to scan
			for (BeanDefinition b : beanDefinitions) {
				Class<?> cl = Class.forName(b.getBeanClassName());
				Field[] fs = cl.getDeclaredFields();
				for (Field f : fs) {
					if (f.isAnnotationPresent(PropertyMatch.class)) {
						map.putIfAbsent(cl.getSuperclass(), new HashMap<>());
						map.get(cl.getSuperclass()).put(f.getName(), b.getBeanClassName());
					}
				}
			}

			Constructor<?> constructor = Class.forName(GenericPolymorphicDeserializer.class.getName())
					.getConstructors()[0];

			for (Class<?> cls : map.keySet()) {
				GenericPolymorphicDeserializer constructorObj = (GenericPolymorphicDeserializer) constructor
						.newInstance(cls);

				for (String identifier : map.get(cls).keySet()) {
					constructorObj.updateSubType(identifier, Class.forName(map.get(cls).get(identifier)));
				}

				SimpleModule module = new SimpleModule(cls.getName());
				module.addDeserializer(cls, constructorObj);
				objectMapper.registerModule(module);
			}

		} catch (Exception e) {
			LOG.error("Execption occurred in register", e);
			throw new RuntimeException(e.getMessage());
		}

	}

}

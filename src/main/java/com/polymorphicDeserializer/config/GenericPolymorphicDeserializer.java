package com.polymorphicDeserializer.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

/**
 * 
 * custom deserializers - which gets invoked in runtime
 * to deserialize to value types registered.  
 * 
 * @author <a href="mailto:rohith.rohith009@gmail.com">Rohith Varala</a>
 *
 * @param <T>
 * 
 * @see com.polymorphicDeserializer.config.DeserializerRegistry
 * @see com.polymorphicDeserializer.annotations.Subtype
 * @see com.polymorphicDeserializer.annotations.PropertyMatch
 */
public class GenericPolymorphicDeserializer<T> extends StdDeserializer<T> { 
	private static final long serialVersionUID = -45460663589894209L;

	private Class<?> baseClass;

	// Registry for unique Identifier and its respective mapping for sub class types
	private Map<String, Class<? extends T>> subTypeRegistry;

	public GenericPolymorphicDeserializer(Class<?> vc) {
		super(vc);
		baseClass = vc;
		subTypeRegistry = new HashMap<>();
	}
	
	/**
	 * This method will used to register all subTypes and identified by a identifier  
	 * @param identifier
	 * @param subType
	 */
	public void updateSubType(String identifier, Class<? extends T> subType) {
		subTypeRegistry.put(identifier, subType);
	}
	
	/** 
     * This method invokes in runtime to deserialize json content into value type.
     * 
     * @param p Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *   this deserialization activity.
     *
     * @return Deserialized value
     */
	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Class<? extends T> subClazz = null;
		
		ObjectMapper mapper = (ObjectMapper) p.getCodec();
		ObjectNode objNode = (ObjectNode) mapper.readTree(p);
		Iterator<Entry<String, JsonNode>> fieldsIterator = objNode.fields();
		
		while(fieldsIterator.hasNext()){
		    Entry<String, JsonNode> pair = fieldsIterator.next();
		    String key = pair.getKey();
		    if (subTypeRegistry.containsKey(key)) {
		    	subClazz = subTypeRegistry.get(key);
		    	break;
		    }
		}
		
		if (subClazz == null) {
			// if no subClass then deserialize to baseClass
			return (T) new Gson().fromJson(objNode.toString(), baseClass);
		}
		// if subClass is found then deserialize to subClazz type
		return mapper.treeToValue(objNode, subClazz);
	}

}

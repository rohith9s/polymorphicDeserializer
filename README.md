# polymorphic Deserializer  


Jackson deserialization/serialization works out of the box most of the time. Unless your JSON structure is complicated, you won’t have to deal with deserializing your JSON into a polymorphic data type.

 
 Below is Employee base class, which inturn have subClasses as `FullTimeEmployee` & `PartTimeEmployee`.
 ```java
    @Setter(AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    @ToString(callSuper=true, includeFieldNames=true)
    public class Employee {
        private Long employeeId;
        private String name;

    }
  ```
  
   ```java
    @Setter(AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    @ToString(callSuper = true, includeFieldNames = true)
    @Subtype // <-------- Custom Annotation: registers as subType of Employee to deserialize in runtime
    public class FullTimeEmployee extends Employee {
      /**
       * Custom Annotation:  if request object contains {@code @PropertyMatch } annotated field attribute, then it will deserialize to 
       * {@code FullTimeEmployee}
       */
      @PropertyMatch
      private double salary;

    }
  ```
  
   ```java
    @Setter(AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    @ToString(callSuper=true, includeFieldNames=true)
    @Subtype // <-------- Custom Annotation: registers as subType of Employee to deserialize in runtime
    public class PartTimeEmployee extends Employee {

      /**
       * Custom Annotation: if request object contains {@code @PropertyMatch } annotated field attribute, then it will deserialize to  {@code PartTimeEmployee}
       */
      @PropertyMatch  
      private double hourlyWage;
    }
  ``` 
  
  ## Problem Statement inspired to dig for solution:
  Lets say we have a requirement to expose an api `/saveEmployee` to persist employee, but let say we have `N` implementations of `Employee` pojo. so we have to expose `N` no. of endpoints to persist employee respectively. Here come's in the Idea of **Polymorphic Deserializer**, using which we will expose only single API to persist different Employee implementations. but it comes with some annotated metadata ` @Subtype` and `@PropertyMatch` in subTypes as shown in above code snippets. 
  
  ```java
    /**
     * Here, we receive request with {@code Employee} or its subTypes
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
    public Employee saveEmployee(@RequestBody Employee employee) { // <--- Deserializes to respective model based on received requestObj
     LOG.info("Received request :" + employee); // 

     return employee;
    }
  ``` 
  
Thats it, your are now ready to expose polymorphic API's, code is self explanatory, where ever required placed comments to understand.
  

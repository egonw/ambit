package ambit2.rules.json;

import java.lang.reflect.Field;

import org.codehaus.jackson.JsonNode;

public class JSONParsingUtils 
{
	
	public static void jsonNodeToJavaField(JsonNode node, Object targetObj, String fieldName, boolean FlagExactFieldTypeMatch) throws Exception
	{
		if (node == null)
			throw (new Exception("JsonNode is null!"));
		
		if (targetObj == null)
			throw (new Exception("Target java object is null!"));
				
		Class cls = targetObj.getClass();
		Field field;
		String fType;
		
		try{
			field = cls.getDeclaredField(fieldName);
			fType = field.getType().getName();	
		}
		catch (Exception e)
		{
			throw (new Exception("Field " + fieldName + " does not exists!") );
		}
			
		
		/*
		if (fType.equals("java.lang.String"))
		{	
			field.set(targetObj, keyValue);
			return;
		}	
		
		if (fType.equals("int"))
		{	
			int intValue = Integer.parseInt(keyValue);
			field.set(targetObj, intValue);
			return;
		}
		
		if (fType.equals("double"))
		{	
			double doubleValue = Double.parseDouble(keyValue);
			field.set(targetObj, doubleValue);
			return;
		}
		*/
		
		throw (new Exception("Unsupported field type: " + fType + " for the field: " + field.getName()));
		
		//TODO ... may be add some other field types 
		
	}
	
	
	
	//-------------- Utils for general extraction of JSON data --------------------- 
	
	
	/**
	 * @param node
	 * @param keyword
	 * @param isRequired
	 * @return String value extracted from the JSON node or empty string if isRecquired = false (this case 
	 * is not treated as an error hence no exception is thrown)
	 * @throws Exception with the JSON error or missing value error (if isRecquired = true) 
	 */
	public static String extractStringKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception ("Keyword " + keyword + " is missing!"));
			
			return "";
		}
		
		if (keyNode.isTextual())
		{	
			return keyNode.asText();
		}
		else
		{	
			throw (new Exception ( "Keyword " + keyword + " is not of type text!"));
		}			
	}
	
	
	/**
	 * 
	 * @param node
	 * @param keyword
	 * @param isRequired
	 * @return Double value extracted from the JSON node or null pointer if isRecquired = false (this case 
	 * is not treated as an error hence no exception is thrown)
	 * @throws Exception with the JSON error or missing value error (if isRecquired = true)
	 */
	public static Double extractDoubleKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception ("Keyword " + keyword + " is missing!"));
			
			return null;
		}
		
		if (keyNode.isDouble())
		{	
			return keyNode.asDouble();
		}
		else
		{	
			throw (new Exception ( "Keyword " + keyword + " is not of type Double!"));
		}			
	}
	
	/**
	 * 
	 * @param node
	 * @param keyword
	 * @param isRequired
	 * @return Integer value extracted from the JSON node or null pointer if isRecquired = false (this case 
	 * is not treated as an error hence no exception is thrown)
	 * @throws Exception with the JSON error or missing value error (if isRecquired = true)
	 */
	public static Integer extractIntKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception ("Keyword " + keyword + " is missing!"));
			
			return null;
		}
		
		if (keyNode.isInt())
		{	
			return keyNode.asInt();
		}
		else
		{	
			throw (new Exception ( "Keyword " + keyword + " is not of type Int!"));
		}			
	}
	
	
	
	
	
}

package ambit2.rules.test;

import org.openscience.cdk.tools.LoggingTool;

import ambit2.rules.conditions.ValueCondition;
import ambit2.rules.conditions.value.Value;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConditionsTests extends TestCase
{
	public LoggingTool logger;
	
	public ConditionsTests() 
	{   
		logger = new LoggingTool(this);
	}
	
	public static Test suite() {
		return new TestSuite(ConditionsTests.class);
	}
	
	void checkValueCondition(double target, String relString, double dValue, boolean expectedResult)
	{
		Value value = new Value (dValue, relString);
		ValueCondition condition = new ValueCondition(value);
		boolean res = condition.isTrue(target);
		assertEquals("condition: " + target + " " + relString + " " + dValue, expectedResult, res);
	}
	
	public void testValueConditions()
	{
		checkValueCondition(3, ">=", 5, false);
		checkValueCondition(3, ">=", -5, true);
		checkValueCondition(3, ">", 1, true);
		checkValueCondition(3, "<", 1, false);
		checkValueCondition(3, "<=", 15, true);
		checkValueCondition(3, "=", 3, true);
		checkValueCondition(3, "!=", 3, false);
	}
	
}

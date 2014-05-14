package ambit2.base.data.study.test;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Value;

public class EffectRecordTest {

	@Test
	public void testStdDev() throws Exception {
		EffectRecord<String,Params,String> effectRecord = new EffectRecord<String,Params,String>();
		effectRecord.setConditions(new Params());
		effectRecord.setStdDev(1,"mm");
		Assert.assertEquals(1.0,effectRecord.getStdDev().getLoValue());
		Assert.assertEquals("mm",effectRecord.getStdDev().getUnits());
	}
	
	@Test
	public void testStdDev_notinitialized() throws Exception {
		EffectRecord<String,Params,String> effectRecord = new EffectRecord<String,Params,String>();
		try {
			effectRecord.setStdDev(1,"mm");
			Assert.fail("Should throw exception instead!");
		} catch (UnsupportedOperationException x) {
		}
	}
	
	@Test
	public void testValue() throws Exception {
		Value value = new Value();
		value.setUnits("mg");
		value.setLoValue(3.14);
		value.setLoQualifier("ca.");
		value.setUpQualifier("<=");
		value.setUpValue("6.28");
		System.out.println(value.toString());
	}

}

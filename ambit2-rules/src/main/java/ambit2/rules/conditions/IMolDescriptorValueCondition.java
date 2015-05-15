package ambit2.rules.conditions;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IMolDescriptorValueCondition extends IValueIntervalCondition
{
	public boolean isTrue(IAtomContainer target);
	public String getDescriptorName();
	public void setDescriptorName(String descriptorName);
}

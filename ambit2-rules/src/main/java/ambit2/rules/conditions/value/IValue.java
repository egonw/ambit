package ambit2.rules.conditions.value;

public interface IValue 
{
	public static enum Relation {
		EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS, DIFFERENT  
	}
	
	public double getValue();
	public void setValue(double value);
	
	public Relation getRelation();
	public void setRelation(Relation relation);
	
}

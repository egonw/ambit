package ambit2.reactions;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class RetroSynthRuleInstance implements IRetroSynthRuleInstance
{	
	IAtomContainer container;
	RetroSynthRule rule;	
	Vector<IAtom> atoms = null;
	double score = 0.0;
	
	@Override
	public Vector<IAtom> getAtoms() {
		return atoms;
	}
	
	@Override
	public IRetroSynthRule getRule() {
		return rule;
	}
	
	@Override
	public IAtomContainer getContainer() {
		return container;
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public double addScore(double scoreIncrement) {
		score += scoreIncrement;
		return score;
	}		
}

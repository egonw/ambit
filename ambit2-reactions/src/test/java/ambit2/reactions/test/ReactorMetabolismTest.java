package ambit2.reactions.test;

import java.io.File;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.reactions.ReactionDataBase;
import ambit2.reactions.reactor.Reactor;
import ambit2.reactions.reactor.ReactorResult;
import ambit2.reactions.reactor.ReactorStrategy;
import ambit2.smarts.SmartsHelper;


public class ReactorMetabolismTest extends TestCase
{
	public LoggingTool logger;
	protected Reactor reactor;
	
	public ReactorMetabolismTest() throws Exception
	{
		logger = new LoggingTool(this);
		setupReactor();
	}
	
	public static Test suite() {
		return new TestSuite(ReactorMetabolismTest.class);
	}
	
	
	protected void setupReactor() throws Exception
	{	
		reactor = new Reactor();
		
		//Object o = new Object();
		URL resource = reactor.getClass().getClassLoader().getResource("ambit2/reactions/metabolism-reactions.json");
		
		
		ReactionDataBase reactDB = new ReactionDataBase(new File(resource.getFile()));
				
		reactDB.configureReactions(reactor.getSMIRKSManager());
		reactor.setReactionDataBase(reactDB);		
		
		ReactorStrategy strategy = new ReactorStrategy(new File(resource.getFile()));  //strategy is in the same file
		
		strategy.FlagStoreFailedNodes = true;
		strategy.FlagStoreSuccessNodes = true;
		strategy.maxNumOfSuccessNodes = 0;  //if 0 then the reactor will stop after the first success node
		
		strategy.FlagCheckNodeDuplicationOnPush = true;
		strategy.FlagTraceReactionPath = true;
		strategy.FlagLogMainReactionFlow = false;
		strategy.FlagLogReactionPath = false;
		strategy.FlagLogNameInReactionPath = false;
		strategy.FlagLogExplicitHToImplicit = true;
		
		reactor.setStrategy(strategy);
		
		//Setup Smirks manager
		reactor.getSMIRKSManager().setFlagProcessResultStructures(true);
		reactor.getSMIRKSManager().setFlagClearImplicitHAtomsBeforeResultProcess(false);
		reactor.getSMIRKSManager().setFlagAddImplicitHAtomsOnResultProcess(false);
		reactor.getSMIRKSManager().setFlagConvertExplicitHToImplicitOnResultProcess(false);
	}
	
	protected boolean metabolize(String smiles) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles, true);
		ReactorResult result = reactor.react(mol);
		return (result.numSuccessNodes > 0);
	}
	
	
	public void testMetabolism01() throws Exception
	{
		String smiles = "CO";
		assertEquals("metabolize " + smiles, true, metabolize(smiles));
		smiles = "CCO";
		assertEquals("metabolize " + smiles, false, metabolize(smiles));
	}
	
	
}

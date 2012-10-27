package ambit2.tautomers.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;
import java.text.DecimalFormat;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.tautomers.TautomerManager;



public class AutomaticTautomerTests 
{
	class CmdOption
	{	
		String option = null;
		String value = null;		
	};	
	
	
	public static final int LPM_PROCESS_NCI = 1;
	public static final int LPM_FILTER = 2;
	public static final int LPM_TAUTOMER_COUNT = 3;
	public static final int LPM_TAUTOMER_EQUIVALENCE = 4;
	public static final int LPM_TAUTOMER_COMPARISON = 5;
	public static final int LPM_CACTVS_COUNT = 6;
	public static final int LPM_TAUTOMER_COUNT_COMBINATORIAL = 7;
	public static final int LPM_TAUTOMER_COUNT_COMBINATORIAL_IMPROVED = 8;
	public static final int LPM_STRUCTURE_STAT = 9;
	public static final int LPM_EQUIVALENCE_STAT = 10;
	
	//public static final int LPM_COMPARE_AMBIT_INTERNAL = 11;
	//public static final int LPM_COMPARE_AMBIT_EXTERNAL = 12;
	
	
	
	
	int lineProcessMode = 0;
		
	String endLine = "\r\n";
	String outFileName = "";
	String inFileName = "";
	String inFileName2 = "";
	String inFileName3 = "";
	String inFileName4 = "";
	String inFileName5 = "";
	
	String command = "";
	String dbFileName = "";
	
	String cactvsExecPath = "C:/Program Files/cactvs/lib/tclcactvs.exe";
	String cactvsOutPath = "D:/Projects/data012-tautomers/cactvs";
	
	
	RandomAccessFile outFile = null;
	TautomerManager tman;
	IsomorphismTester isoTester = new IsomorphismTester();
	//SmartsParser sp = new SmartsParser();
	
	//Tracing variables
	int curLine = 0;	
	int curProcessedStr = 0;
	int nInputStr = 0;
	int nStartStr = 0;
	int traceFrequency = 10;
	
	int minNumOfTautomerForChecking = 2;
	int maxNumOfTautomerForChecking = 20;
	int maxNumOfTautomerForFullCheck = 10;
	int nEquivalenceErrors = 0;
	int nEquivalenceTests = 0;
	
	int tutomerErrorSum = 0;
	
	//Filters
	int fMinNA = -1;
	int fMaxNA = -1;
	int fMinNDB = -1;
	int fMaxNDB = -1;
	int fMinCyclo = -1;
	int fMaxCyclo = -1;
	
	
	//Structure statistics helpers
	int StrSizeBins[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100};
	int NumRingBins[] = {0,1,2,3,4,5,6,7,8,9,10};
	int freqStrSizeBins[];
	int freqNumRingBins[];
	
	//Equivalnece statistics helpers
	double RelNumUneqBins[] = {0,1,2,3,4,5,6,7,8,9,10,15,20,25,30,40,50,60,70,80,90,100};  //in percent
	int AbsNumUneqBins[] = {0,1,2,3,4,5,6,7,8,9,10,15,20,25,30,40,50,60,70,80,90,100};
	double UneqDiffBins [] = {0,1, 2, 3, 4, 5, 6 , 7, 8, 9, 10, 15, 20, 30};
	
	int freqRelNumUneqBins[];
	int freqAbsNumUneqBins[];
	int freqUneqDiffBins [];
	
	
	
	public static boolean FlagCheckMemory = false;
	
	public static void main(String[] args)
	{
		AutomaticTautomerTests att = new AutomaticTautomerTests();
				
		try {
			att.handleArguments(new String[] {
					//"-i","D:/Projects/data012-tautomers/nci-filtered_max_cyclo_4.smi",
					
					"-i","D:/Projects/data012-tautomers/results-final/Ambit-Tautomer-Count-Canonical-Incremental-FULL.txt",
					"-i2","D:/Projects/data012-tautomers/results-final/Ambit-Tautomer-Count-Canonical-Incremental-FULL.txt",
					
					"-nInpStr","0",
					"-nStartStr","1",
					"-c","compare-algorithms",
					"-o","D:/Projects/data012-tautomers/test.txt",
					"-fMinNDB", "1",
					"-fMaxCyclo", "4",
			});
			
			att.handleCommand();
		} 
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
		
	public int handleArguments(String[] args) throws Exception
	{
		if (args.length == 0)
		{	
			printHelp();
			return(0);
		}	
		
		Vector<CmdOption> options = extractOptions(args);
		
		System.out.println("AutomaticTautomerTests  started with options:");
		for (int i = 0; i < options.size(); i++)
			System.out.println("  " + options.get(i).option + "  " + options.get(i).value);
		
		CmdOption opt;
		
		opt = getOption("i", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName = opt.value; 
		}
		
		opt = getOption("i2", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName2 = opt.value; 
		}
		
		opt = getOption("i3", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName3 = opt.value; 
		}
		
		opt = getOption("4", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName4 = opt.value; 
		}
		
		opt = getOption("i5", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName5 = opt.value; 
		}
		
		opt = getOption("o", options);
		if (opt != null)
		{
			if (opt.value != null)
				outFileName = opt.value; 
		}
		
		opt = getOption("db", options);
		if (opt != null)
		{
			if (opt.value != null)
				dbFileName = opt.value; 
		}	
		
		opt = getOption("c", options);
		if (opt != null)
		{
			if (opt.value != null)
				command = opt.value; 
		}	
		
		opt = getOption("nInpStr", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					nInputStr = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect nInpStr value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("nStartStr", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					nStartStr = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect nStartStr value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		//filters
		opt = getOption("fMinNA", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMinNA = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMinNA value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMaxNA", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMaxNA = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMaxNA value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMinNDB", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMinNDB = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMinNDB value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMaxNDB", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMaxNDB = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMaxNDB value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMinCyclo", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMinCyclo = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMinCyclo value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		
		opt = getOption("fMaxCyclo", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMaxCyclo = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMaxCyclo value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		
		return (0);
	}
	
	
	
	public int handleCommand() throws Exception
	{
		if (command == null)
			return(0);
		
		
		if (command.equals("process-nci"))
		{
			System.out.println("Processing nci file: " + inFileName);
			openOutputFile();
			lineProcessMode = LPM_PROCESS_NCI;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("filter"))
		{
			System.out.println("Filtering file: " + inFileName);
			openOutputFile();
			lineProcessMode = LPM_FILTER;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-count"))
		{
			System.out.println("tautomer counting: " + inFileName);
			openOutputFile();
			setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_COUNT;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-count-comb"))
		{
			System.out.println("tautomer counting: " + inFileName);
			openOutputFile();
			setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_COUNT_COMBINATORIAL;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-count-comb-improved"))
		{
			System.out.println("tautomer counting: " + inFileName);
			openOutputFile();
			setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_COUNT_COMBINATORIAL_IMPROVED;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-equivalence"))
		{
			System.out.println("Checking tautomer equivalence: " + inFileName);
			openOutputFile();			
			lineProcessMode = LPM_TAUTOMER_EQUIVALENCE;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		
		if (command.equals("cactvs-count"))
		{
			System.out.println("Counting cactvs tautomers: " + inFileName);
			openOutputFile();			
			lineProcessMode = LPM_CACTVS_COUNT;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("structure-stat"))
		{
			System.out.println("Making structure statistics: " + inFileName);
			openOutputFile();
			initStructureStatistics();
			lineProcessMode = LPM_STRUCTURE_STAT;
			iterateInputFile();
			printStructureStatistics();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("equivalence-stat"))
		{
			System.out.println("Making equivalence statistics: " + inFileName);
			openOutputFile();
			initEquivalenceStatistics();
			lineProcessMode = LPM_EQUIVALENCE_STAT;
			iterateInputFile();
			printEquivalenceStatistics();
			closeOutputFile();
			return(0);
		}
		
		
		if (command.equals("compare-algorithms"))
		{
			System.out.println("compare + \n first input file: " + inFileName);
			System.out.println("second input file: " + inFileName2);
			
			openOutputFile();			
			//lineProcessMode = LPM_COMPARE_AMBIT_INTERNAL;
			
			compareAlgorithms();
			
			closeOutputFile();
			return(0);
		}
		
		
		
		System.out.println("Unknown command: " + command);
		
		return(-1);
	}	
	
	
	public void printHelp()
	{
		System.out.println("Program for automatic testing of tautomer generation algorithms");
		System.out.println("-h            print help info");
		System.out.println("-i            input file");
		System.out.println("-i<n>         additional input files n = 2,3,4,5");
		System.out.println("-o            output file");
		System.out.println("-nInpStr      the number of used input structures");
		System.out.println("-nStartStr    the number of starting structure");
		
		System.out.println("-c            command: ");
		System.out.println("                 process-nci           nci file is processed");
		System.out.println("                 filter                input file is filtered");
		System.out.println("                 tautomer-count        counts the number of tautomers");
		System.out.println("                 tautomer-equivalence  check the equivalence of each tautomer");
		System.out.println("                 cactvs-count          counts the number of cactvs tautomers");
		
	}	
	
	
	public Vector<CmdOption> extractOptions(String[] args)
	{		
		CmdOption curOption = null;		
		Vector<CmdOption> v = new Vector<CmdOption>();
		if (args.length == 0)
			return(v);
		
		
		for (int i = 0; i < args.length; i++)
		{	
			if (args[i].startsWith("--"))
			{
				if (curOption != null)				
					v.add(curOption);
				
				curOption = new CmdOption();
				curOption.option = args[i].substring(2);
			}
			else
				if (args[i].startsWith("-"))
				{
					if (curOption != null)				
						v.add(curOption);
					
					curOption = new CmdOption();
					curOption.option = args[i].substring(1);
				}
				else
				{
					if (curOption == null) //This is the first argument and it is not an option
					{
						curOption = new CmdOption();
						curOption.option = "";
						curOption.value = args[i];
					}
					else
					{	
						if (curOption.value == null)
							curOption.value = args[i];
						else					
							curOption.value += " " + args[i];
					}	
				}
		}
		
		v.add(curOption);
		
		
		return(v);
	}
	
	
	CmdOption getOption(String opt, Vector<CmdOption> options)
	{
		for (int i = 0; i < options.size(); i++)
		{
			if (options.get(i).option.equals(opt))
				return (options.get(i));
		}
		
		return null;
	}
	
	
	//Input - Output handling 
	
	int openOutputFile()
	{
		try
		{
			File file = new File(outFileName);
			outFile = new RandomAccessFile(file,"rw");
			outFile.setLength(0);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		if (outFile == null)
			System.out.println("Incorrect outFile");
		
		return(0);
	}
	
	
	int closeOutputFile() 
	{
		try
		{
			if (outFile != null)
				outFile.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		return(0);
	}
	
	int output(String data)
	{
		try
		{
			outFile.write(data.getBytes());
		}
		catch (Exception e)
		{
			System.out.println("output error: " + e.toString());
			return(-1);
		}
		return(0);
	}
	
	void iterateInputFile()
	{	
		try
		{	
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			int n = 0;
			curProcessedStr = 0;
			while (f.getFilePointer() < length)
			{	
				n++;
				curLine = n;
				
				String line = f.readLine();
				//System.out.println("line " + n + "  " + line);
				
				if (n < nStartStr)
					continue;
				
				curProcessedStr++;
				
				if (nInputStr > 0)
					if (curProcessedStr > nInputStr) 
						break;
												
				
				processLine(line.trim());
				
				if (FlagCheckMemory)
				{
					checkMemory();				
					Runtime.getRuntime().gc();
				}	
				
				if (n % this.traceFrequency == 0)
					System.out.println(n);
			}
			
			f.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());
		}
	}
	
	int processLine(String line)
	{	
		if (lineProcessMode == LPM_PROCESS_NCI)
		{
			processNCILine(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_FILTER)
		{
			filterLine(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_COUNT)
		{
			tautomerCount(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_COUNT_COMBINATORIAL)
		{
			tautomerCountCombinatorial(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_COUNT_COMBINATORIAL_IMPROVED)
		{
			tautomerCountCombinatorialImproved(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_EQUIVALENCE)
		{
			tautomerEquivalence(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_CACTVS_COUNT)
		{
			cactvsCount(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_STRUCTURE_STAT)
		{
			structureStatistics(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_EQUIVALENCE_STAT)
		{
			equivalenceStatistics(line);
			return(0);
		}
		
		return 0;
	}
	
	
	//------------- process line functions (implementation of different commands) ------------------
	
	int processNCILine(String line)
	{
		//System.out.println(line);
		Vector<String> tokens = filterTokens(line.split(" "));
		
		if (tokens.size() < 2)
		{	
			System.out.println("Line " + curLine + "  has less than 2 tokens.");
			return (-1);
		}
		
		if (tokens.size() > 2)
		{	
			System.out.println("Line " + curLine + "  has more than 2 tokens.");
		}	
		
		
		try
		{
			//Create molecule from SMILES
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(tokens.get(1));
			
			//Generate new SMILES
			java.io.StringWriter result =  new java.io.StringWriter();
			SMILESWriter writer = new SMILESWriter(result);			
			writer.write(mol);
			writer.close();
			
			//System.out.println(tokens.get(1) + "  -->  " + result.toString());
			String smiles = result.toString();
			int dot_index = smiles.indexOf(".");
			if (dot_index == -1)
				output(smiles);
			else
				output(smiles.substring(0, dot_index));
			
		}
		catch(Exception e){			
		}
		
		return (0);
	}
	
	int filterLine(String line)
	{
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			if (isFilterOK(mol))
				output(line + endLine);
		}	
		catch(Exception e){
			return (-1);
		}
		
		return (0);
	}
	
	
	boolean isFilterOK(IAtomContainer mol)
	{
		if (fMinNA != -1)
		{
			if (mol.getAtomCount() < fMinNA)
				return(false);
		}
		
		if (fMaxNA != -1)
		{
			if (mol.getAtomCount() > fMaxNA)
				return(false);
		}
		
		
		int NDB = -1; //this value means that NDB has not been evaluated yet
		
		if (fMinNDB != -1)
		{
			//calculation of NDB
			NDB = 0;
			for (IBond bond: mol.bonds())
			{
				if (bond.getOrder() == IBond.Order.DOUBLE)
					NDB++;
			}
			
			if (NDB < fMinNDB)
				return(false);
		}
		
		if (fMaxNDB != -1)
		{
			if (NDB == -1)
			{
				//calculation of NDB
				NDB = 0;
				for (IBond bond: mol.bonds())
				{
					if (bond.getOrder() == IBond.Order.DOUBLE)
						NDB++;
				}
			}
			
			if (NDB > fMaxNDB)
				return(false);
		}
		
		
		if (fMinCyclo != -1)
		{
			if ((mol.getBondCount() - mol.getAtomCount() + 1) < fMinCyclo)
				return(false);
		}
		
		if (fMaxCyclo != -1)
		{
			if ((mol.getBondCount() - mol.getAtomCount() + 1) > fMaxCyclo)
				return(false);
		}
		
		return true;
	}
	
	int tautomerCount(String line)
	{
		System.out.println("" + curLine + "   " + line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			IAtomContainer can_t = TautomerManager.getCanonicTautomer(resultTautomers);
			
			output("" + curLine + "   " + line + "  " + resultTautomers.size() + "  " +  
					SmartsHelper.moleculeToSMILES(can_t)  /* +  endLine */);
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return (0);
	}
	
	int tautomerCountCombinatorial(String line)
	{
		System.out.println("" + curLine + "   " + line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
			//IAtomContainer can_t = TautomerManager.getCanonicTautomer(resultTautomers);
			
			output("" + curLine + "   " + line + "  " + resultTautomers.size() + endLine);   
				//	+ "  " + SmartsHelper.moleculeToSMILES(can_t)  /* +  endLine */);
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return (0);
	}
	
	int tautomerCountCombinatorialImproved(String line)
	{
		System.out.println("" + curLine + "   " + line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomers_ImprovedCombApproach();
			//IAtomContainer can_t = TautomerManager.getCanonicTautomer(resultTautomers);
			
			output("" + curLine + "   " + line + "  " + resultTautomers.size() + endLine);   
				//	+ "  " + SmartsHelper.moleculeToSMILES(can_t)  /* +  endLine */);
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return (0);
	}
	
	
	int tautomerEquivalence(String line)
	{
		System.out.println(line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			setTautomerManager();
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			
			if (resultTautomers.size() < minNumOfTautomerForChecking)
				return 0;
			
			if (resultTautomers.size() > maxNumOfTautomerForChecking)
				return 0;
			
			int checkRes = checkTautomerEquivalence(resultTautomers);
			nEquivalenceTests++;
			
			DecimalFormat df = new DecimalFormat("##0.0");
			
			double meanTErr = 0;
			if (checkRes > 0 )
				meanTErr = (1.0*tutomerErrorSum / checkRes);
			String meanTErr_fs = df.format(meanTErr);
			
			
			output(line + "  " + resultTautomers.size() +  "   " + checkRes + "   "  + meanTErr_fs + endLine);
			
			if (checkRes > 0)
				nEquivalenceErrors++;
			
			System.out.println("n = " + curLine + "   nTests = " + nEquivalenceTests + 
					"   nErrors = " + nEquivalenceErrors);
		}	
		catch(Exception e){
			return (-1);
		}
		
		return (0);
	}
	
	int checkTautomerEquivalence(Vector<IAtomContainer> tautomers) throws Exception
	{
		//Vector<String> codes = new Vector<String>();
		//for (int i = 0; i < tautomers.size(); i++)
		//	codes.add(TautomerManager.getTautomerCodeString(tautomers.get(i)));
		
		int numErr = 0;
		tutomerErrorSum = 0;
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			if (i > this.maxNumOfTautomerForFullCheck)
				break;
			
			tman.setStructure(tautomers.get(i));
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
					
			//boolean FlagOK = compareTautomerSets(codes, resultTautomers);
			int numOfDiffTautomers = compareTautomerSetsIsomorphismCheck(tautomers, resultTautomers);
			
			
			if (numOfDiffTautomers > 1)
			{
				numErr++;
				tutomerErrorSum += numOfDiffTautomers;
			}
				
		}
		
		
		return numErr;
	}
	
	boolean compareTautomerSets(Vector<String> codes, Vector<IAtomContainer> tautomers)
	{
		//This check is very simple and give wrong results when there are 
		//isomorphic tautomers (typically filtered in the results) 
		
		if (codes.size() != tautomers.size())
			return false;
		
		int CheckedTautomers = 0;
		for (int i = 0; i < tautomers.size(); i++)
		{
			String code = TautomerManager.getTautomerCodeString(tautomers.get(i), false);
			for (int k = 0; k < codes.size(); k++)
				if (code.equals(codes.get(k)))
				{
					CheckedTautomers++;
					break;
				}
		}
		
		if (CheckedTautomers < codes.size() )
			return false;
		else
			return true;
	}
	
	
	int compareTautomerSetsIsomorphismCheck(Vector<IAtomContainer> tautomers0, Vector<IAtomContainer> tautomers)
	{
		//if (tautomers0.size() != tautomers.size())
		//	return false;
		
		int numberOfDiff = 0;
		
		for (int i = 0; i < tautomers0.size(); i++)
		{
			QueryAtomContainer query = SmartsHelper.getQueryAtomContainer(tautomers0.get(i), false);
			isoTester.setQuery(query);
			
			boolean FlagFound = false;
			for (int k = 0; k < tautomers.size(); k++)
			{
				//sp.setSMARTSData() is not needed for this isomorphism
				boolean res = isoTester.hasIsomorphism(tautomers.get(k));
				if (res)
				{
					FlagFound = true;
					tautomers.remove(k);
					break;
				}
			}
			
			//if (!FlagFound)
			//	return(false);
			if (!FlagFound)
				numberOfDiff++;
		}
		
		return numberOfDiff;
	}
	
	
	int cactvsCount(String line)
	{
		System.out.println(line);
		
		try
		{	
			//Prepare cactvs tcl script
			String tclFile = cactvsOutPath + "/cactvs-work-temp" + curLine + ".tcl";
			String cactvsOutFile = cactvsOutPath + "/cactvs-work-temp" + curLine + ".smi";			
			createCactvsTclScript(line, tclFile, cactvsOutFile);
			
			//Execute cactvs command			
			Runtime rt = Runtime.getRuntime();
			
			String command = "cmd /c start \"" + "122"  + "\" /wait " + "\"" + cactvsExecPath + "\" -f " + tclFile;
			
			System.out.println(command);
			
			Process cactvs = rt.exec(command);
			
			//Handle cactvs out
			//int nTautomers = handleCactvsOut(cactvsOutFile);			
			//System.out.println("nTautomers = " + nTautomers);
			
		}	
		catch(Exception e){
			return (-1);
		}
		
		return (0);
	}
	
	int createCactvsTclScript(String smiles, String fileName, String cactvsOutFile)
	{
		try
		{
			File file = new File(fileName);
			RandomAccessFile f = new RandomAccessFile(fileName,"rw");
			f.setLength(0);
			String data = "molfile write [molfile create " 
				+ cactvsOutFile + " a] [ens get [ens create {" 
				+ smiles + "}] E_TAUTOSET]";
			f.write(data.getBytes());
			f.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			return(-1);
		}
		
		return (0);
	}
	
	int handleCactvsOut(String fileName) 
	{
		int nT = 0;
		try
		{
			File file = new File(fileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			curProcessedStr = 0;

			while (f.getFilePointer() < length)
			{	
				String line = f.readLine();
				//System.out.println(">>> " + line);
				nT++;
			}

			f.close();
		}
		catch(Exception e)
		{	
		}
		
		return (nT);
	}
	
	
	//---------------- statistics and comparison methods ----------------------
	
	
	int getBinIndex(int value, int bins[])
	{
		for (int i = 0; i < bins.length; i++)
		{
			if (value <= bins[i])
				return i;
		}
		return (bins.length-1); //last bin by default - it means the the value is larger the last bin value
	}
	
	int getBinIndex(double value, double bins[])
	{
		for (int i = 0; i < bins.length; i++)
		{
			if (value <= bins[i])
				return i;
		}
		return (bins.length-1); //last bin by default - it means the the value is larger the last bin value
	}
	
	
	//---------------
	
	void initStructureStatistics()
	{
		freqNumRingBins = new int [NumRingBins.length];
		freqStrSizeBins = new int [StrSizeBins.length];
		
		for (int  i = 0; i < freqNumRingBins.length; i++)
			freqNumRingBins[i] = 0;
		
		for (int  i = 0; i < freqStrSizeBins.length; i++)
			freqStrSizeBins[i] = 0;
	}
	
	int structureStatistics(String line)
	{	
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			int nAt = mol.getAtomCount();
			int nRing = mol.getBondCount() - mol.getAtomCount() + 1;
			
			int nAtBinIndex = getBinIndex(nAt, StrSizeBins);
			freqStrSizeBins[nAtBinIndex]++;
			
			int nRingBinIndex = getBinIndex(nRing,NumRingBins);
			freqNumRingBins[nRingBinIndex]++;
						
			output(line + "  " + nAt + "   " + nRing + endLine);
			
			//System.out.println(mol.getAtomCount());
		}	
		catch(Exception e){
			System.out.println("Exception at structut " + line );
			return (-1);
		}
		return 0;
	}
	
	void printStructureStatistics()
	{
		System.out.println("Structure Size Histogram:  bin   frequency");
		for (int i = 0; i < StrSizeBins.length; i++)
			System.out.println("  " + StrSizeBins[i] + "  " + freqStrSizeBins[i]);
		
		System.out.println("Number of Rings Histogram:  bin   frequency");
		for (int i = 0; i < NumRingBins.length; i++)
			System.out.println("  " + NumRingBins[i] + "  " + freqNumRingBins[i]);
	}
	
	//---------------
	
	void initEquivalenceStatistics()
	{	
		freqAbsNumUneqBins = new int [AbsNumUneqBins.length];
		freqRelNumUneqBins = new int [RelNumUneqBins.length];
		freqUneqDiffBins = new int [UneqDiffBins.length];
		
		
		for (int  i = 0; i < freqAbsNumUneqBins.length; i++)
			freqAbsNumUneqBins[i] = 0;
		
		for (int  i = 0; i < freqRelNumUneqBins.length; i++)
			freqRelNumUneqBins[i] = 0;
		
		for (int  i = 0; i < freqUneqDiffBins.length; i++)
			freqUneqDiffBins[i] = 0;
	}
	
	int equivalenceStatistics(String line)
	{	
		try
		{
			Vector<String> tokens = filterTokens(line.split(" "));
			int n = tokens.size();
			
			if (n != 4)
			{	
				System.out.println("Error at " + line);
				return -1;
			
			}

			int nTaut = Integer.parseInt(tokens.get(1));
			int nUneq = Integer.parseInt(tokens.get(2));
			double relNUneq = (100.0*nUneq)/nTaut;  //in percent
			double meanDiff = Double.parseDouble(tokens.get(3));
			
			//Updating the bins frequencies 
			int ANUBinIndex = getBinIndex(nUneq, AbsNumUneqBins);
			freqAbsNumUneqBins[ANUBinIndex]++;
			
			int RNUBinIndex = getBinIndex(relNUneq, RelNumUneqBins);
			freqRelNumUneqBins[RNUBinIndex]++;
			
			int diffBinIndex = getBinIndex(meanDiff, UneqDiffBins);
			freqUneqDiffBins[diffBinIndex]++;
			

		}	
		catch(Exception e){
			System.out.println("Exception at struct " + line + endLine + e.toString());
			return (-1);
		}
		
		return 0;
	}
	
	void printEquivalenceStatistics()
	{	
		System.out.println("Histogram(Absolute Number of Unequivalent Tautomers):  bin   frequency");
		for (int i = 0; i < AbsNumUneqBins.length; i++)
			System.out.println("  " + AbsNumUneqBins[i] + "  " + freqAbsNumUneqBins[i]);
		
		System.out.println("Histogram(Relative Number of Unequivalent Tautomers):  bin   frequency");
		for (int i = 0; i < RelNumUneqBins.length; i++)
			System.out.println("  " + RelNumUneqBins[i] + "  " + freqRelNumUneqBins[i]);
		
		System.out.println("Histogram(Unequivalent Mean Difference):  bin   frequency");
		for (int i = 0; i < UneqDiffBins.length; i++)
			System.out.println("  " + UneqDiffBins[i] + "  " + freqUneqDiffBins[i]);
		
	}
	
	
	int compareAlgorithms()
	{
		//Creating bins
		int n = 20;
		int DiffBins[] = new int[2*n+1];
		int freqDiffBins[] = new int [2*n+1];
		
		int bNum = 0;
		for (int i = -n; i<=n; i++)
		{	
			DiffBins[bNum] = i;
			freqDiffBins[bNum] = 0;
			bNum++;
		}
		
		
		//TODO
		
		
		
		return 0;
	}
	
	
	/*
	int compareAmbitToOtherSoftwareTools()
	{
		//TODO
		return 0;
	}
	*/
	
	//--------------------------------------------------------
	
	public Vector<String> filterTokens(String tokens[])
	{
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < tokens.length; i++)
			if (!tokens[i].equals(""))
				v.add(tokens[i]);
		return v;
	}
	
	void setTautomerManager()
	{
		tman = new TautomerManager();
		tman.use15ShiftRules(true);
		tman.use17ShiftRules(false);
		tman.maxNumOfBackTracks = 10000;
	}
	
	void checkMemory()
	{
		long totalMem = Runtime.getRuntime().totalMemory() / 1000000;
		long freeMem = Runtime.getRuntime().freeMemory() / 1000000;
		long maxMem = Runtime.getRuntime().maxMemory() / 1000000;
		
		long usedMem = totalMem - freeMem;
		
		System.out.println("usedMem = " + usedMem + "   freeMem = " + freeMem + "  totalMem = " + totalMem 
				+ "    maxMem = " + maxMem);
	}
	
}

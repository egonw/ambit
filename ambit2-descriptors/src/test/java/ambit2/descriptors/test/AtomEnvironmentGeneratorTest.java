package ambit2.descriptors.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.descriptors.AtomEnvironment;
import ambit2.descriptors.AtomEnvironmentMatrixDescriptor;
import ambit2.descriptors.processors.AtomEnvironmentGenerator;
import ambit2.descriptors.processors.AtomEnvironmentList;

public class AtomEnvironmentGeneratorTest {
	protected CDKHydrogenAdder hAdder = null;
	   /*
	    public void testAtomEnvironmentDescriptor() throws Exception {
	    	AtomEnvironmentGenerator gen = new AtomEnvironmentGenerator();
	    	gen.setMaxLevels(1);
	    	gen.setUseHydrogens(false);
	    	
		    SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
 	        IAtomContainer mol = sp.parseSmiles("CCCC(O)=O");
			AtomConfigurator typer = new AtomConfigurator();
			typer.process(mol);
			    
			CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(NoNotificationChemObjectBuilder.getInstance());
			hAdder.addImplicitHydrogens(mol);

			mol = gen.process(mol);
			
			Object ae = mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
			
			Assert.assertTrue(ae instanceof AtomEnvironmentList);
			for (AtomEnvironment a : (AtomEnvironmentList) ae) {
				System.out.println(a);
				int[] l0 = a.getLevel(0);
				for (int i:l0) System.out.print(String.format("%d,", i));
				System.out.println();
				int[] l1 = a.getLevel(1);
				for (int i:l1) System.out.print(String.format("%d,", i));
				System.out.println();
			}
			System.out.println(ae);
			    
	    }
	   */
	   @Test
		public void test() throws Exception {
			
			AtomEnvironmentGenerator gen = new AtomEnvironmentGenerator();
			gen.setMaxLevels(1);
			InputStream in = AtomEnvironmentGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
			RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				record = gen.process(record);
				AtomEnvironmentList ae = (AtomEnvironmentList) record.getProperty(gen.getProperty());
				System.out.println();
				System.out.println(ae);
				
				for (AtomEnvironment a : (AtomEnvironmentList) ae) {
					System.out.println(a);
					int[] l0 = a.getLevel(0);
					for (int i:l0) System.out.print(String.format("%d,", i));
					System.out.println();
					int[] l1 = a.getLevel(1);
					for (int i:l1) System.out.print(String.format("%d,", i));
					System.out.println();
				}
				
			}
			reader.close();
			/*
			IStructureRecord record = new StructureRecord();
			record.setContent();
			MoleculeReader reader = new MoleculeReader();
			reader.process(target)
			*/
		}	   
	   
	   @Test
		public void testAtomTypeMatrix() throws Exception {
			
			AtomEnvironmentMatrixDescriptor gen = new AtomEnvironmentMatrixDescriptor();
			InputStream in = AtomEnvironmentGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
			IIteratingChemObjectReader<IAtomContainer> reader = new MyIteratingMDLReader(new InputStreamReader(in),SilentChemObjectBuilder.getInstance());
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);				
                //if (useHydrogens) { //always, otherwise atom types are not recognised correctly
                	//for some reason H atoms are added as bond references, but not in atom list - bug?
				try {
	    			if (hAdder == null) hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    		    hAdder.addImplicitHydrogens(mol);
				} catch (Exception x) {
					
				}
    			CDKHueckelAromaticityDetector.detectAromaticity(mol);        
				Hashtable<String,Integer> sparseMatrix = gen.doCalculation(mol);
			    System.out.println("Sparse Matrix");
			    System.out.println(sparseMatrix);
			    System.out.println("End Sparse Matrix");
			}
			reader.close();
		}	   	
	   @Test
		public void testAtomTypeMatrixDescriptor() throws Exception {
			
			AtomEnvironmentMatrixDescriptor gen = new AtomEnvironmentMatrixDescriptor();
			InputStream in = AtomEnvironmentGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
			IIteratingChemObjectReader<IAtomContainer> reader = new MyIteratingMDLReader(new InputStreamReader(in),SilentChemObjectBuilder.getInstance());
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);				
               //if (useHydrogens) { //always, otherwise atom types are not recognised correctly
               	//for some reason H atoms are added as bond references, but not in atom list - bug?
				try {
	    			if (hAdder == null) hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    		    hAdder.addImplicitHydrogens(mol);
				} catch (Exception x) {	}
				CDKHueckelAromaticityDetector.detectAromaticity(mol);        
				DescriptorValue value = gen.calculate(mol);
			    System.out.println("Value");
			    for (int i=0; i < value.getNames().length; i++)  
			    	if (((IntegerArrayResult)value.getValue()).get(i)>0) {
			    		System.out.println(value.getNames()[i] + " = " + ((IntegerArrayResult)value.getValue()).get(i));		
			    	}
			    System.out.println("End Value");
			}
			reader.close();
		}	   
	   
	   
		public void runAtomTypeMatrixDescriptor(String root) throws Exception {
			AtomEnvironmentMatrixDescriptor gen = new AtomEnvironmentMatrixDescriptor();
			InputStream in = new FileInputStream(root+"tox_benchmark_N6512.sdf");
			IIteratingChemObjectReader<IAtomContainer> reader = new MyIteratingMDLReader(new InputStreamReader(in),SilentChemObjectBuilder.getInstance());
			//matrix market sparse
			String mmfile = root+"tox_benchmark_N6512_AEMATRIX.mm.tmp";
			FileWriter mmwriter = new FileWriter(mmfile);
			int mmrows = 0; int mmcols = 0; int mmentries = 0;
			
			Hashtable<String, FileWriter> writers = new Hashtable<String, FileWriter>();
			String[] sets = new String[] {"ALL"};
			for (String set:sets) {
				FileWriter writer = new FileWriter(root + "tox_benchmark_"+set+"_AEMATRIX.csv");
				writers.put(set, writer);
			}
			boolean header = false;
			while (reader.hasNext()) {
				
				IAtomContainer mol = reader.next();
				String set = mol.getProperty("Set").toString();
				FileWriter writer = writers.get("ALL");

				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);				
              //if (useHydrogens) { //always, otherwise atom types are not recognised correctly
              	//for some reason H atoms are added as bond references, but not in atom list - bug?
				try {
	    			if (hAdder == null) hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    		    hAdder.addImplicitHydrogens(mol);
				} catch (Exception x) {
					
				}
	  			CDKHueckelAromaticityDetector.detectAromaticity(mol);        
	  			DescriptorValue value = gen.calculate(mol);
	  			if (!header) {
	  				for (int i=0; i < value.getNames().length; i++) {
						//attr.write(value.getNames()[i]);
						//attr.write('\n');
	  					writer.write('"');
	  					writer.write(value.getNames()[i]);
	  					writer.write('"');
	  					writer.write(",");
	  				}
	  				writer.write("Activity,Set");
	  				writer.write('\n');
	  				header = true;
	  				mmcols = value.getNames().length;
	  				//attr.close();
	  			}	
	  			//row

				for (int i=0; i < value.getNames().length; i++) {
				   	int count = ((IntegerArrayResult)value.getValue()).get(i);
				   	writer.write(Integer.toString(count));
				   	writer.write(",");
				}
				double activity = Double.parseDouble(mol.getProperty("Activity").toString());
	  			writer.write(activity==1.0?"Yes":"No");
	  			writer.write(",");
	  			writer.write(set);
				writer.write('\n');
				writer.flush();
				
				mmrows++;
				for (int i=0; i < value.getNames().length; i++) {
				   	int count = ((IntegerArrayResult)value.getValue()).get(i);
				   	if (count<=0) continue;
				   	mmentries++;
					mmwriter.write(String.format("%d\t%d\t%d\n",mmrows,(i+1),count));
				}
				
			}
			reader.close();
			for (String set:sets) {
				writers.get(set).close();
			}

			mmwriter.close();
			mmwriter = new FileWriter(root+"tox_benchmark_N6512_AEMATRIX.mm");
			mmwriter.write("%%MatrixMarket matrix coordinate real general\n");
			mmwriter.write(String.format("%d\t%d\t%d\n",mmrows,mmcols,mmentries));
			BufferedReader bin=null;
			try {
				String line;
				bin = new BufferedReader(new FileReader(new File(mmfile)));
				while ((line = bin.readLine()) != null) { 
					mmwriter.write(line);
					mmwriter.write("\n");
				}

			} catch (Exception x) {
				x.printStackTrace();
			} finally {
				bin.close();
				mmwriter.close();
			}
			
			
		}	 	  
	   
	   public static void main(String[] args) {
		   AtomEnvironmentGeneratorTest test = new  AtomEnvironmentGeneratorTest();
		   String root = args[0];
		   try {
			   test.runAtomTypeMatrixDescriptor(root);
		   } catch (Exception x) {
			   x.printStackTrace();
		   }
	   }
}

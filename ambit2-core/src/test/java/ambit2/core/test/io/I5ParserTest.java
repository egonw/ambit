package ambit2.core.test.io;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.I5ReaderSimple;
import ambit2.core.io.RawIteratingFolderReader;
import ambit2.core.io.ZipReader;

public class I5ParserTest {
	@Test
	public void test() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/i5d/ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3_0.i5d");
		I5ReaderSimple reader = new I5ReaderSimple(in);
		int count = 0;
		int foundCas=0;
		int foundName=0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			/*
			for (Property p : record.getProperties())
				System.out.println(String.format("%s [%s] = %s",p.getName(),p.getReference().getTitle(),record.getProperty(p)));
			System.out.println();
			*/
			count++;
			for (Property p :record.getProperties()) {
				foundCas += record.getProperty(p).equals("59-87-0")?1:0;
				foundName += record.getProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getProperty(p));
			}
			Assert.assertNotNull(record.getSmiles());
			Assert.assertNotNull(record.getContent());
			Assert.assertNotNull(record.getProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(1,count);
		Assert.assertEquals(1,foundCas);
		Assert.assertEquals(1,foundName);
	}	
	
	
	@Test
	public void testi5z() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/i5z/RefSub_030913110311.zip");
		ZipReader reader = new ZipReader(in);
		int count = 0;
		int foundCas=0;
		int foundName=0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			/*
			for (Property p : record.getProperties())
				System.out.println(String.format("%s [%s] = %s",p.getName(),p.getReference().getTitle(),record.getProperty(p)));
			System.out.println();
			*/
			count++;
			for (Property p :record.getProperties()) {
				foundCas += record.getProperty(p).equals("59-87-0")?1:0;
				foundName += record.getProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getProperty(p));
			}
			//Assert.assertNotNull(record.getSmiles());
			//Assert.assertNotNull(record.getInchi());
			//Assert.assertNotNull(record.getProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(10,count);
		Assert.assertEquals(1,foundCas);
		Assert.assertEquals(1,foundName);
	}		
	
	@Test
	public void testi5dFolder() throws Exception {
		URL url  = getClass().getClassLoader().getResource("ambit2/core/data/i5z/RefSub_030913110311");
		
		File dir = new File(url.getFile());
		
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("i5d");
			}
		});
		Assert.assertEquals(20,files.length);
		RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
		int count = 0;
		int foundInChI=0;
		int foundCas=0;
		int foundName=0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			/*
			for (Property p : record.getProperties())
				System.out.println(String.format("%s [%s] = %s",p.getName(),p.getReference().getTitle(),record.getProperty(p)));
			System.out.println();
			*/
			count++;
			if (record.getContent()!=null && "INC".equals(record.getFormat())) {
				foundInChI++;
				System.out.println(record.getContent());
			}
			for (Property p :record.getProperties()) {
				foundCas += record.getProperty(p).equals("59-87-0")?1:0;
				foundName += record.getProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getProperty(p));
			}
			//Assert.assertNotNull(record.getSmiles());
			//Assert.assertNotNull(record.getInchi());
			//Assert.assertNotNull(record.getProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(10,count);
		Assert.assertEquals(10,foundInChI);
	}	
}
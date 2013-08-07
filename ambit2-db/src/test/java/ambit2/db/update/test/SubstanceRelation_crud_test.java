package ambit2.db.update.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.substance.relation.DeleteSubstanceRelation;
import ambit2.db.substance.relation.UpdateSubstanceRelation;
import ambit2.db.update.IQueryUpdate;

public class SubstanceRelation_crud_test extends CRUDTest<SubstanceRecord,StructureRecord>{

	@Override
	protected IQueryUpdate<SubstanceRecord,StructureRecord> createQuery() throws Exception {
		SubstanceRecord c1 = new SubstanceRecord();
		c1.setIdsubstance(1);
		StructureRecord c2= new StructureRecord();
		c2.setIdchemical(10);
		Proportion p = new Proportion();
		p.setReal_lower(">=");
		p.setReal_lowervalue(5.0);
		p.setReal_upper("<=");
		p.setReal_unit("%");
		p.setReal_uppervalue(15.0);
		p.setTypical_value(10.0);
		p.setTypical("c.a.");
		p.setTypical_unit("%");
		return new UpdateSubstanceRelation(c1,c2,STRUCTURE_RELATION.HAS_CONSTITUENT,p);
	}

	@Override
	protected void createVerify(IQueryUpdate<SubstanceRecord,StructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT idsubstance,idchemical,relation,proportion_real_unit,proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_typical,proportion_typical_value,proportion_typical_unit FROM substance_relation where idsubstance=1 and idchemical=10 and relation='HAS_CONSTITUENT'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(">=",table.getValue(0,"proportion_real_lower"));
		Assert.assertEquals(5.0,table.getValue(0,"proportion_real_lower_value"));
		Assert.assertEquals("<=",table.getValue(0,"proportion_real_upper"));
		Assert.assertEquals(15.0,table.getValue(0,"proportion_real_upper_value"));
		Assert.assertEquals("%",table.getValue(0,"proportion_real_unit"));
		Assert.assertEquals("c.a.",table.getValue(0,"proportion_typical"));
		Assert.assertEquals(10.0,table.getValue(0,"proportion_typical_value"));
		Assert.assertEquals("%",table.getValue(0,"proportion_typical_unit"));
		c.close();
	}


	protected IQueryUpdate<SubstanceRecord,StructureRecord> createQueryNew() throws Exception {
		SubstanceRecord c1 = new SubstanceRecord();
		c1.setIdsubstance(1);
		StructureRecord c2= new StructureRecord();
		c2.setIdchemical(7);
		Proportion p = new Proportion();
		p.setReal_lower(">=");
		p.setReal_lowervalue(5.0);
		p.setReal_upper("<=");
		p.setReal_unit("%");
		p.setReal_uppervalue(15.0);
		p.setTypical_value(10.0);
		p.setTypical("c.a.");
		p.setTypical_unit("%");
		p.setFunction("test");
		return new UpdateSubstanceRelation(c1,c2,STRUCTURE_RELATION.HAS_ADDITIVE,p);
	
	}
	
	protected void createVerifyNew(IQueryUpdate<SubstanceRecord,StructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT idsubstance,idchemical,relation,function,proportion_real_unit,proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_typical,proportion_typical_value,proportion_typical_unit FROM substance_relation where idsubstance=1 and idchemical=7 and relation='HAS_ADDITIVE'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("test",table.getValue(0,"function"));
		Assert.assertEquals(">=",table.getValue(0,"proportion_real_lower"));
		Assert.assertEquals(5.0,table.getValue(0,"proportion_real_lower_value"));
		Assert.assertEquals("<=",table.getValue(0,"proportion_real_upper"));
		Assert.assertEquals(15.0,table.getValue(0,"proportion_real_upper_value"));
		Assert.assertEquals("%",table.getValue(0,"proportion_real_unit"));
		Assert.assertEquals("c.a.",table.getValue(0,"proportion_typical"));
		Assert.assertEquals(10.0,table.getValue(0,"proportion_typical_value"));
		Assert.assertEquals("%",table.getValue(0,"proportion_typical_unit"));
		c.close();
	}	

	@Override
	protected IQueryUpdate<SubstanceRecord,StructureRecord> deleteQuery() throws Exception {
		SubstanceRecord c1 = new SubstanceRecord();
		c1.setIdsubstance(1);
		StructureRecord c2= new StructureRecord();
		c2.setIdchemical(11);
		DeleteSubstanceRelation q = new DeleteSubstanceRelation(c1,c2,STRUCTURE_RELATION.HAS_ADDITIVE);
		return q;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<SubstanceRecord,StructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idsubstance,idchemical,relation FROM substance_relation where idsubstance=1 and idchemical=11 and relation='HAS_ADDITIVE'");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	public void testUpdate() throws Exception {
		//do nothing
	}

	@Override
	protected IQueryUpdate<SubstanceRecord,StructureRecord> updateQuery()
			throws Exception {
		return null;
	}

	@Override
	protected void updateVerify(IQueryUpdate<SubstanceRecord,StructureRecord> query)
			throws Exception {
	}

}
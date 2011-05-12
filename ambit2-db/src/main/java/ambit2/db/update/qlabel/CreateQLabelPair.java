package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

/**
 * Find minority  per source
 * SELECT idchemical,idstructure,if(q.label!="Majority","",if(num_sources=(rel+1),"Majority","Minority")),
rel,num_sources,label,q.text,p.text FROM quality_chemicals q
join quality_pair p using(idchemical)
order by idchemical
 * @author nina
 *
 */
public class CreateQLabelPair extends AbstractUpdate<AmbitUser, String> {
	protected static String[] sql = {
		"insert ignore into roles (role_name) values (\"ambit_quality\");",
		"insert ignore into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (?,\"d66636b253cb346dbb6240e30def3618\",\"quality\",\"Automatic quality verifier\",now(),\"confirmed\",\"quality\",\"http://ambit.sourceforge.net\");",
		"insert ignore into user_roles (user_name,role_name) values (?,\"ambit_quality\");",

		//"LOCK TABLES quality_pair WRITE , quality_chemicals WRITE, fp1024_struc READ \n",
		
		"delete from quality_pair\n",
		
		"insert into quality_pair (idchemical,idstructure,rel,user_name,updated,`text`)\n"+
		"select s1.idchemical,s1.idstructure id1,\n"+
		"s1.fp1=s2.fp1 && s1.fp2=s2.fp2 && s1.fp3=s2.fp3 && s1.fp4=s2.fp4 && s1.fp5=s2.fp5 && s1.fp6=s2.fp6 &&\n"+
		"s1.fp7=s2.fp7 && s1.fp8=s2.fp8 && s1.fp9=s2.fp9 && s1.fp10=s2.fp10 && s1.fp11=s2.fp11 && s1.fp12=s2.fp12 &&\n"+
		"s1.fp13=s2.fp13 && s1.fp14=s2.fp14 && s1.fp15=s2.fp15 && s1.fp16=s2.fp16 as Q\n"+
		",?,CURRENT_TIMESTAMP(),\n"+
		"CASE 1 WHEN\n"+
		"(s1.fp1=s2.fp1 && s1.fp2=s2.fp2 && s1.fp3=s2.fp3 && s1.fp4=s2.fp4 && s1.fp5=s2.fp5 && s1.fp6=s2.fp6 &&\n"+
		"s1.fp7=s2.fp7 && s1.fp8=s2.fp8 && s1.fp9=s2.fp9 && s1.fp10=s2.fp10 && s1.fp11=s2.fp11 && s1.fp12=s2.fp12 &&\n"+
		"s1.fp13=s2.fp13 && s1.fp14=s2.fp14 && s1.fp15=s2.fp15 && s1.fp16=s2.fp16) = 0\n"+
		"THEN cast(s1.idstructure as char) ELSE concat_WS(',',cast(s1.idstructure as char),cast(s2.idstructure as char)) END\n"+
		"from fp1024_struc s1\n"+
		"join fp1024_struc s2\n"+
		"join struc_dataset d1 on s1.idstructure=d1.idstructure\n"+
		"join struc_dataset d2 on s2.idstructure=d2.idstructure\n"+
		"join structure sa on s1.idstructure=sa.idstructure\n"+
		"join structure sb on s2.idstructure=sb.idstructure\n"+
		"join src_dataset ds1 on d1.id_srcdataset=ds1.id_srcdataset\n"+
	    "join src_dataset ds2 on d2.id_srcdataset=ds2.id_srcdataset\n"+		
		"where s1.idchemical = s2.idchemical and s1.idstructure != s2.idstructure and s1.status='valid' && s2.status='valid'\n"+
		"and sa.type_structure != 'MARKUSH'\n"+
		"and sa.type_structure != 'NA'\n"+
		"and sb.type_structure != 'MARKUSH'\n"+
		"and sb.type_structure != 'NA'\n"+		
		"and ds1.user_name != 'guest' and ds2.user_name != 'guest'\n"+
		"on duplicate key update " +
		"rel=IF(FIND_IN_SET(s2.idstructure,`text`)=0,rel+values(rel),rel),\n"+ //don't count same structure multiple times
		"user_name=values(user_name),\n"+
		"updated=CURRENT_TIMESTAMP(),\n"+
		"`text`=CASE 1 WHEN values(rel)=0 THEN `text`  " +
		"ELSE " +
		"IF (FIND_IN_SET(s2.idstructure,`text`)=0,concat_WS(',',`text`,cast(s2.idstructure as char)),`text`) " +
		"END",

		/*
		DbCreateDatabase.func[0],
		DbCreateDatabase.func[1],
		*/
		"update quality_pair set text=sortstring(text)\n",
		
		"delete from quality_chemicals",
		
		
		"insert into quality_chemicals (idchemical,num_sources,label,num_structures,text)\n"+
		"select idchemical,1,'Unconfirmed',1,'1' from\n"+
		"(\n"+
		"SELECT count(distinct(idstructure)) c ,idchemical FROM structure\n"+ //count distinct structures in datasets, otherwise in case of ToxCast same structure is counted multiple times
		"join struc_dataset using(idstructure)\n"+
		"join src_dataset using(id_srcdataset)\n"+
		"where type_structure != 'NA'\n"+
		"and type_structure != 'MARKUSH'\n"+
		"and src_dataset.user_name != 'guest'\n"+ //restrict to datasets uploaded by users other than guest
		"group by idchemical\n"+
		") A where c=1\n",


		"insert into quality_chemicals (idchemical,num_sources,label,num_structures,text)\n"+
		"SELECT idchemical,count(distinct(text)) c,'Consensus',1,(rel+1) FROM quality_pair\n"+
		"group by idchemical,text\n"+
		"on duplicate key update\n"+
		"num_structures = num_structures+1,\n"+
		"`label`=CASE 1 WHEN values(num_sources)=num_sources THEN 'Ambiguous'  WHEN values(num_sources)<num_sources THEN 'Majority' ELSE 'Majority' END,\n"+
		"`text`=IF(FIND_IN_SET(values(`text`),`text`)=0,concat_ws(',',`text`,values(`text`)),`text`),\n"+
		"num_sources=CASE 1 WHEN num_sources<=values(num_sources) THEN values(num_sources) ELSE num_sources END\n",
		
		
		
		//"DROP TEMPORARY TABLE 'quality_pair'\n",
		//"delete from quality_pair\n",
		
		"insert ignore into roles (role_name) values (\"ambit_quality\");",
		"insert ignore into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (\"comparison\",\"d66636b253cb346dbb6240e30def3618\",\"quality\",\"Automatic comparisong between sources\",now(),\"confirmed\",\"comparison\",\"http://ambit.sourceforge.net\");",
		"insert ignore into user_roles (user_name,role_name) values (\"comparison\",\"ambit_quality\");",
		
		"update quality_chemicals set text=replace(sortstring(text),',',':')\n",
		
		"delete from quality_structure where user_name='comparison'\n",
		
		"insert into quality_structure (idstructure,user_name,label,text,updated)\n"+
		"SELECT idstructure,'comparison',\n"+
		"if (q.label='Consensus','OK',\n"+
		"  if (q.label='Majority',if ((num_sources=(rel+1)),'ProbablyOK','ProbablyERROR'),'Unknown')\n"+
		"),\n"+
		"\'Comparison between different sources',current_timestamp()\n"+
		" FROM quality_chemicals q\n"+
		"join quality_pair p using(idchemical)\n",
		
		"insert ignore into quality_structure (idstructure,user_name,label,text,updated)\n"+
		"select idstructure,'comparison','Unknown','single source',current_timestamp() from structure\n"+
		"join\n"+
		"(\n"+
		"SELECT idchemical,count(distinct(id_srcdataset)) c FROM structure\n"+
		"join struc_dataset using(idstructure)\n" +
		"join src_dataset using(id_srcdataset)\n" +
		"where type_structure != 'NA' and type_structure != 'MARKUSH' and src_dataset.user_name!='guest'\n" +
		"group by idchemical\n"+
		") as SS\n"+
		"using(idchemical)\n"+
		"where c=1\n"+
		"and type_structure != 'NA'\n"+
		"and type_structure != 'MARKUSH'\n",
		//"UNLOCK TABLES"
	
		
		"insert into structure (idchemical,idstructure,structure,atomproperties,user_name,`preference`)\n"+
		"select structure.idchemical,idstructure,structure,atomproperties,structure.user_name,100*q.label+(10-type_structure) from quality_structure q join structure using(idstructure)\n"+
		"on duplicate key update `preference`=values(`preference`)\n",

		"delete from quality_pair"
	};
		

	public CreateQLabelPair() {
		super();
		setGroup(new AmbitUser("quality"));
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {

		if ((index == 1) || (index == 2) || (index == 4)) {
			List<QueryParam> p = new ArrayList<QueryParam>();
			if (getGroup() ==null) setGroup(new AmbitUser("quality"));
			p.add(new QueryParam<String>(String.class,getGroup().getName()));
			return p;
		} else return null;
	}

	public String[] getSQL() throws AmbitException {
		//for (String s: sql) System.out.println(s);
		return sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
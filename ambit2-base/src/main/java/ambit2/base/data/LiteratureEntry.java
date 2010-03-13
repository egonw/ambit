/**
 * Created on 2005-3-21
 *
 */
package ambit2.base.data;




/**
 * A Literature entry: <br>
 * title, URL
 *   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class LiteratureEntry extends AmbitBean implements ILiteratureEntry{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3089173811051934066L;
	public final static String p_title="title";
	public final static String p_url="URL";
	protected String title;
	protected String URL;
    protected int id = -1;
    protected boolean editable;
    protected _type type = _type.BibtexEntry;
    public _type getType() {
		return type;
	}
	public void setType(_type type) {
		this.type = type;
	}
	protected static String EINECS_name = "EINECS";
    protected static String IUPAC_name = "IUPAC name";
    protected static String CAS_num = "CAS Registry Number";
    protected static String Default_name = "Default";
    protected static String AMBIT_uri = "http://ambit.sourceforge.net";
    protected static String EINECS_uri = "http://ec.europa.eu/environment/chemicals/exist_subst/einecs.htm";
/*
	private static java.util.concurrent.CopyOnWriteArrayList<LiteratureEntry> references = 
		new CopyOnWriteArrayList<LiteratureEntry>();
		*/

	public static synchronized LiteratureEntry getInstance() {
		return getInstance(Default_name,AMBIT_uri);
	}	
	public static synchronized LiteratureEntry getInstance(String name) {
		return getInstance(name,AMBIT_uri);
	}
	public static synchronized LiteratureEntry getCASReference() {
		return getInstance(CAS_num,AMBIT_uri);
	}
	public static synchronized LiteratureEntry getIUPACReference() {
		return getInstance(IUPAC_name,AMBIT_uri);
	}	
	
	public static synchronized LiteratureEntry getEINECSReference() {
		return getInstance(EINECS_name,EINECS_uri);
	}
	public static synchronized LiteratureEntry getInstance(String name,String url) {
		return getInstance(name,url,-1);
	}
	public static synchronized LiteratureEntry getInstance(String name,String url, int id) {
		LiteratureEntry et = new LiteratureEntry(name,url);
		et.setId(id);
		return et;
	}	    
	public String getTitle() {
		return title;
	}

	public String getURL() {
		return URL;
	}
	/**
	 * We need separate instances for web services
	 * @param title
	 * @param url
	 */
	public LiteratureEntry(String title, String url) {
		this.title = (title.length()>255)?title.substring(1,255):title;
		this.URL = (url.length()>255)?url.substring(1,255):url;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getTitle());
		buf.append(getURL());
		return buf.toString();
	}
    public int getId() {
        return id;
    }
    public String getName() {
        return getTitle();
    }
    public boolean hasID() {
        return id > 0;
    }
    public void setId(int id) {
        this.id = id;
        
    }
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof LiteratureEntry)
    		return ((LiteratureEntry)obj).getTitle().equals(getTitle());
    	else return false;
    }
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == getName() ? 0 : getTitle().hashCode());
    	hash = 31 * hash + var_code; 

    	return hash;
    }	    


}

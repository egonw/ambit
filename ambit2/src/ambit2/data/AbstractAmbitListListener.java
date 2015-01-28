/**
 * <b>Filename</b> AbstractAmbitListListener.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-7-21
 * <b>Project</b> ambit
 */
package ambit2.data;

/**
 * Abstract listener for {@link ambit2.data.AmbitListChanged} event. 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-7-21
 */
public abstract class AbstractAmbitListListener implements IAmbitListListener {

    /**
     * 
     */
    public AbstractAmbitListListener() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see ambit2.data.IAmbitListListener#ambitListChanged(ambit2.data.AmbitListChanged)
     */
    public abstract void ambitListChanged(AmbitListChanged event);

    /* (non-Javadoc)
     * @see ambit2.data.IAmbitObjectListener#ambitObjectChanged(ambit2.data.AmbitObjectChanged)
     */
    public abstract void ambitObjectChanged(AmbitObjectChanged event);

}


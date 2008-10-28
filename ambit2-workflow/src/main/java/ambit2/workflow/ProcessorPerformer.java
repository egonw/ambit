/* ProcessorPerformer.java
 * Author: Nina Jeliazkova
 * Date: Apr 13, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.workflow;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;
import ambit2.core.processors.IProcessor;

import com.microworkflow.execution.Performer;

public class ProcessorPerformer<P extends IProcessor<Target,Result>,Target,Result> 
				extends Performer<Target,Result> implements PropertyChangeListener {
    public static String errorTag=DBWorkflowContext.ERROR;
    protected P processor;
    public ProcessorPerformer(P processor) {
        setProcessor(processor);
    }
    public synchronized P getProcessor() {
        return processor;
    }
    public synchronized void setProcessor(P processor) {
        if (this.processor instanceof DefaultAmbitProcessor) 
        	((DefaultAmbitProcessor)this.processor).removePropertyChangeListener(this); 
        this.processor = processor;
        if (processor instanceof DefaultAmbitProcessor) 
        	((DefaultAmbitProcessor)processor).addPropertyChangeListener(this);
    }
    @Override
    public Result execute() {
        if (processor == null) return null;
        try {
            
            return processor.process(getTarget());
        } catch (AmbitException e) {
            context.put(errorTag, e);
            return null;
        }
    }
	public void propertyChange(PropertyChangeEvent evt) {
		context.put(evt.getPropertyName(),null);
		context.put(evt.getPropertyName(),evt.getNewValue());
	}

}

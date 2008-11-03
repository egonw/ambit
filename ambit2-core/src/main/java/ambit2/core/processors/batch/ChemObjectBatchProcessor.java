/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

/**
 * 
 */
package ambit2.core.processors.batch;

import java.io.IOException;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.io.IInputState;
import ambit2.core.processors.IProcessor;

/**
 * Batch processing for {@link IChemObject}
 * @author nina
 *
 */
public class ChemObjectBatchProcessor<Result> extends BatchProcessor<IChemObject, Result> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3155934516387228672L;
	public ChemObjectBatchProcessor() {
		super();
	}
	public ChemObjectBatchProcessor(IProcessor<IChemObject,Result> processor) {
		super(processor);
	}		
	@Override
	protected void closeIterator(Iterator iterator) throws AmbitException {
		if (iterator instanceof IIteratingChemObjectReader)
			try {
				((IIteratingChemObjectReader)iterator).close();
			} catch (IOException x) {
				throw new AmbitException(x);
			}
		
	}

	@Override
	protected Iterator getIterator(IInputState target) throws AmbitException {
		return target.getReader();
	}

}

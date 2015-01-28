/* IDistanceFunction.java
 * Author: Nina Jeliazkova
 * Date: Feb 4, 2007 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit2.similarity;

public interface IDistanceFunction<T> {
    /**
     * Distance should be large if objects are "far away" from each other, i.e. dissimilar.
     * @param object1
     * @param object2
     * @return
     * @throws Exception
     */
    public float getDistance(T object1,T object2) throws Exception;
    /**
     * Calculates native comparison index between two objects (distance or similarity).
     * Similarity is large if objects are similar to each other, in contrast with {@link #getDistance(Object, Object)}.
     * @param object1
     * @param object2
     * @return
     * @throws Exception
     */
    public float getNativeComparison(T object1,T object2) throws Exception;
    
}

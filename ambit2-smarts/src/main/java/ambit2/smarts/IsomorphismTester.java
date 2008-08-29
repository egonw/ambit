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
package ambit2.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import java.util.Vector;
import java.util.Stack;
import java.util.List;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class IsomorphismTester 
{
	QueryAtomContainer query;
	IAtomContainer target;
	boolean isomorphismFound;
	Stack<Node> stack = new Stack<Node>();
	Vector<IAtom> targetAt = new Vector<IAtom>(); //a work container
	Vector<SequenceElement> sequence = new Vector<SequenceElement>();
	Vector<IQueryAtom> sequencedAtoms = new Vector<IQueryAtom>();
	Vector<IQueryAtom> sequencedBondAt1 = new Vector<IQueryAtom>();
	Vector<IQueryAtom> sequencedBondAt2 = new Vector<IQueryAtom>();
	
	public void setQuery(QueryAtomContainer container)
	{
		query = container;
		TopLayer.setAtomTopLayers(query, TopLayer.TLProp);
		setQueryAtomSequence(null);
	}
	
	public Vector<SequenceElement> getSequence()
	{
		return(sequence);
	}
	
	void setQueryAtomSequence(IQueryAtom firstAt)
	{	
		IQueryAtom firstAtom;
		SequenceElement seqEl;
		TopLayer topLayer;
		//Vector<IQueryAtom> curAddedAtoms = new Vector<IQueryAtom>();  
		int n;
		
		if (firstAt == null)
			firstAtom = (IQueryAtom)query.getFirstAtom();
		else
			firstAtom = firstAt;
		sequence.clear();
		sequencedAtoms.clear();
		sequencedBondAt1.clear();
		sequencedBondAt2.clear();
		
		//Set first sequence atom
		sequencedAtoms.add(firstAtom);		
		seqEl = new SequenceElement();
		seqEl.center = firstAtom;
		topLayer = (TopLayer)firstAtom.getProperty(TopLayer.TLProp);
		n = topLayer.atoms.size();
		seqEl.atoms = new IQueryAtom[n];
		seqEl.bonds = new IQueryBond[n];
		for (int i = 0; i < n; i++)
		{
			sequencedAtoms.add((IQueryAtom)topLayer.atoms.get(i));
			seqEl.atoms[i] = (IQueryAtom)topLayer.atoms.get(i);
			seqEl.bonds[i] = (IQueryBond)topLayer.bonds.get(i);
			addSeqBond(seqEl.center,seqEl.atoms[i]);
		}
		sequence.add(seqEl);
		
		//Sequencing the entire query structure
		Stack<SequenceElement> stack = new Stack<SequenceElement>();
		stack.push(seqEl);
		while (!stack.empty())
		{
			//curAddedAtoms.clear();
			SequenceElement curSeqAt = stack.pop();
			for (int i = 0; i < curSeqAt.atoms.length; i++)
			{
				topLayer = (TopLayer)curSeqAt.atoms[i].getProperty(TopLayer.TLProp);
				if (topLayer.atoms.size() == 1)
					continue; // it is terminal atom and no further sequencing should be done
				int a[] = getSeqAtomsInLayer(topLayer);
				
				n = 0;
				for (int k = 0; k<a.length; k++)
					if (a[k] == 0)
						n++;
				
				if (n > 0)
				{	
					seqEl = new SequenceElement();
					seqEl.center = curSeqAt.atoms[i];
					seqEl.atoms = new IQueryAtom[n];
					seqEl.bonds = new IQueryBond[n];
					sequence.add(seqEl);
					stack.add(seqEl);
				}	
				
				int j = 0;				
				for (int k = 0; k < a.length; k++)
				{
					if (a[k] == 0)
					{	
						seqEl.atoms[j] = (IQueryAtom)topLayer.atoms.get(k);
						seqEl.bonds[j] = (IQueryBond)topLayer.bonds.get(k);
						addSeqBond(seqEl.center,seqEl.atoms[j]);
						sequencedAtoms.add(seqEl.atoms[j]);
						//curAddedAtoms.add(seqEl.atoms[j]);
						j++;
					}
					else
					{	
						if (curSeqAt.center == topLayer.atoms.get(k))
							continue;
						//Check whether  bond(curSeqAt.atoms[i]-topLayer.atoms.get(k))
						//is already sequenced
						if (getSeqBond(curSeqAt.atoms[i],topLayer.atoms.get(k)) != -1)
							continue;						
						//topLayer.atoms.get(k) atom is already sequenced.
						//Therefore sequnce element of 'bond' type is registered.						
						//newSeqEl is not added in the stack (this is not needed for this bond)
						SequenceElement newSeqEl = new SequenceElement();						
						newSeqEl.center = null;
						newSeqEl.atoms = new IQueryAtom[2];
						newSeqEl.bonds = new IQueryBond[1];
						newSeqEl.atoms[0] = curSeqAt.atoms[i];
						newSeqEl.atoms[1] = (IQueryAtom)topLayer.atoms.get(k);
						addSeqBond(newSeqEl.atoms[0],newSeqEl.atoms[1]);
						newSeqEl.bonds[0] = (IQueryBond)topLayer.bonds.get(k);
						sequence.add(newSeqEl);						
					}
				}
			}			
		}
		
		for(int i = 0; i < sequence.size(); i++)
			sequence.get(i).setAtomNums(query);
	}
		
	boolean containsAtom(Vector<IQueryAtom> v, IQueryAtom atom)
	{
		for(int i = 0; i < v.size(); i++)
			if (v.get(i) == atom)
				return(true);
		return(false);
	}
	
	boolean containsAtom(IAtom[] a, IAtom atom)
	{
		for(int i = 0; i < a.length; i++)
			if (a[i] == atom)
				return(true);
		return(false);
	}
	
	int[] getSeqAtomsInLayer(TopLayer topLayer)
	{
		int a[] = new int[topLayer.atoms.size()];
		for (int i = 0; i <topLayer.atoms.size(); i++)
		{	
			if (containsAtom(sequencedAtoms,(IQueryAtom)topLayer.atoms.get(i)))
			{	
				a[i] = 1;
			}	
			else
				a[i] = 0;
		}	
		return(a);
	}
	
	void addSeqBond(IQueryAtom at1, IQueryAtom at2)
	{
		sequencedBondAt1.add(at1);
		sequencedBondAt2.add(at2);
	}
	
	int getSeqBond(IAtom at1, IAtom at2)
	{
		for (int i = 0; i < sequencedBondAt1.size(); i++)
		{
			if (sequencedBondAt1.get(i)==at1)
			{
				if (sequencedBondAt2.get(i)==at2)
					return(i);
			}
			else
				if (sequencedBondAt1.get(i)==at2)
				{
					if (sequencedBondAt2.get(i)==at1)
						return(i);
				}
		}
		return(-1);		
	}
	
	public boolean hasIsomorphism(IAtomContainer container)
	{	
		target = container;
		TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		executeSequence();
		return(isomorphismFound);
	}
	
	void executeSequence()
	{	
		isomorphismFound = false;
		stack.clear();
				
		//Initial nodes
		SequenceElement el = sequence.get(0);		
		for(int k = 0; k < target.getAtomCount(); k++)
		{
			IAtom at = target.getAtom(k);			
			if(el.center.matches(at))
			{	
				Node node = new Node();
				node.sequenceElNum = 0; 
				node.nullifyAtoms(query.getAtomCount());
				node.atoms[el.centerNum] = at;
				stack.push(node);
			}	
		}
		
		//Expanding the tree of all possible mappings 
		while (!stack.isEmpty())
		{
			expandNode(stack.pop());
			if (isomorphismFound)
				break;
		}
	}
	
	void expandNode(Node node)
	{	
		//System.out.println(node.toString(target));		
		SequenceElement el = sequence.get(node.sequenceElNum);
		
		if (el.center == null) //This node describers a bond that closes a ring
		{
			//Cheking whether this bond is present in the target
			IAtom tAt0 = node.atoms[query.getAtomNumber(el.atoms[0])]; 
			IAtom tAt1 = node.atoms[query.getAtomNumber(el.atoms[1])];
			IBond tBo = target.getBond(tAt0,tAt1);
			if (tBo != null)
				if (el.bonds[0].matches(tBo))
				{
					node.sequenceElNum++;
					stack.push(node);
					if (node.sequenceElNum == sequence.size())
						isomorphismFound = true;
				}	
		}
		else
		{
			targetAt.clear();
			IAtom tAt = node.atoms[el.centerNum];
			List<IAtom> conAt = target.getConnectedAtomsList(tAt);
			for (int i = 0; i < conAt.size(); i++)
			{
				if (!containsAtom(node.atoms,conAt.get(i)))
					targetAt.add(conAt.get(i));
			}
			
			if (el.atoms.length <= targetAt.size())			
				generateNodes(node);
		}
	}
	
	
	void generateNodes(Node node)
	{
		SequenceElement el = sequence.get(node.sequenceElNum);
		
		if (el.atoms.length == 1)
		{
			for(int i = 0; i < targetAt.size(); i++)
			{
				if (el.atoms[0].matches(targetAt.get(i)))
				if(	matchBond(node, el, 0, targetAt.get(i)))
				{
					Node newNode = node.cloneNode();
					newNode.atoms[el.atomNums[0]] = targetAt.get(i);
					newNode.sequenceElNum = node.sequenceElNum+1;
					stack.push(newNode);
					if (newNode.sequenceElNum == sequence.size())
						isomorphismFound = true;
				}
			}
			return;
		}
		
		if (el.atoms.length == 2)
		{
			for(int i = 0; i < targetAt.size(); i++)			
				if (el.atoms[0].matches(targetAt.get(i)))	
				if(	matchBond(node, el, 0, targetAt.get(i)))
					for(int j = 0; j < targetAt.size(); j++)						
						if (i != j)
							if (el.atoms[1].matches(targetAt.get(j)))
							if(	matchBond(node, el, 1, targetAt.get(j)))	
							{
								Node newNode = node.cloneNode();
								newNode.atoms[el.atomNums[0]] = targetAt.get(i);
								newNode.atoms[el.atomNums[1]] = targetAt.get(j);
								newNode.sequenceElNum = node.sequenceElNum+1;
								stack.push(newNode);
								if (newNode.sequenceElNum == sequence.size())
									isomorphismFound = true;
							}					
			return;
		}
		
		if (el.atoms.length == 3)
		{
			for(int i = 0; i < targetAt.size(); i++)			
				if (el.atoms[0].matches(targetAt.get(i)))
				if(	matchBond(node, el, 0, targetAt.get(i)))	
					for(int j = 0; j < targetAt.size(); j++)						
						if (i != j)
							if (el.atoms[1].matches(targetAt.get(j)))
								if(	matchBond(node, el, 1, targetAt.get(j)))	
								for(int k = 0; k < targetAt.size(); k++)
									if ((k != i) && (k != j))
										if (el.atoms[2].matches(targetAt.get(k)))
										if(	matchBond(node, el, 2, targetAt.get(k)))	
										{
											Node newNode = node.cloneNode();
											newNode.atoms[el.atomNums[0]] = targetAt.get(i);
											newNode.atoms[el.atomNums[1]] = targetAt.get(j);
											newNode.atoms[el.atomNums[2]] = targetAt.get(k);
											newNode.sequenceElNum = node.sequenceElNum+1;
											stack.push(newNode);
											if (newNode.sequenceElNum == sequence.size())
												isomorphismFound = true;
										}
			return;
		}
		
		//This case should be very rare (el.atoms.length >= 4)
				
		//a stack which is used for obtaining all
		//posible mappings between el.atoms and targetAt
		//The stack element is an array t[], where t[k] means that 
		//el.atoms[k] is mapped against atom targetAt(t[k])
		//t[t.lenght-1] is used as a work variable which describes how mamy 
		//element of the t array are mapped
		Stack<int[]> st = new Stack<int[]>();
				
		//System.out.println("el.atoms.length = " + el.atoms.length );
		
		//Stack initialization
		for(int i = 0; i < targetAt.size(); i++)
		{
			if (el.atoms[0].matches(targetAt.get(i)))
			if(	matchBond(node, el, 0, targetAt.get(i)))	
			{
				int t[] = new int[el.atoms.length+1];
				t[t.length-1] = 1;
				t[0] = i;				
				st.push(t);
			}
		}
		
		while (!st.isEmpty())
		{
			int t[] = st.pop();
			int n = t[t.length-1];
			if (n == t.length-1)
			{
				//new node
				Node newNode = node.cloneNode();
				for(int k = 0; k < t.length-1; k++)
					newNode.atoms[el.atomNums[k]] = targetAt.get(k);				
				newNode.sequenceElNum = node.sequenceElNum+1;
				stack.push(newNode);
				if (newNode.sequenceElNum == sequence.size())
					isomorphismFound = true;
				continue;
			}
			
			for(int i = 0; i < targetAt.size(); i++)
			{
				//Check whether i is among first elements of t
				boolean Flag = true;
				for (int k = 0; k < n; k++)
					if ( i == t[k]) 
					{
						Flag = false;
						break;
					}
				
				if (Flag)
					if (el.atoms[n].matches(targetAt.get(i)))
					if(	matchBond(node, el, n, targetAt.get(i)))	
					{	
						//new stack element
						int tnew[] = new int[el.atoms.length+1];
						for(int k = 0; k < n; k++)
							tnew[k] = t[k];
						tnew[n] = i;
						tnew[t.length-1] = n+1;
						st.push(tnew);
						
					}
			}
		}
		
	}
	
	boolean matchBond(Node node, SequenceElement el, int qAtNum, IAtom taAt)
	{
		IBond taBo = target.getBond(taAt, node.atoms[el.centerNum]);
		return(el.bonds[qAtNum].matches(taBo));
	}
	
	//public Vector getAllIsomorphisms(IAtomContainer container)
	//{
	//	Vector res = new Vector();
	//	return(res);
	//}
	
	public void printDebugInfo()
	{
		System.out.println("Query Atoms Topological Layers");
		for (int i = 0; i < query.getAtomCount(); i++)						
			System.out.println(""+i+"  "+
					query.getAtom(i).getProperty(TopLayer.TLProp).toString());
		
		System.out.println();
		System.out.println("Query Sequence");
		for (int i = 0; i < sequence.size(); i++)
			System.out.println(sequence.get(i).toString(query));
	}
}

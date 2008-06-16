/***************************************
 * Copyright (c) 2008
 * Ole Eckermann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package org.b3mn.poem;

import javax.persistence.*;

@Entity
public class Structure {

	@Id
	private String hierarchy;
	private int ident_id;
	
	public String getHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	public int getIdent_id() {
		return ident_id;
	}
	public void setIdent_id(int ident_id) {
		this.ident_id = ident_id;
	}
	
	public static Structure instance(int owner_id, String hierarchy) {
		Structure structure = (Structure) Persistance.getSession().
		createSQLQuery("select * from ensure_descendant(:hierarchy,:id)").
		addEntity("structure", Structure.class).
		setString("hierarchy", hierarchy).
		setInteger("id", owner_id).
		uniqueResult();
		Persistance.commit();
		
		return structure;
	}
	
	
	
}
	
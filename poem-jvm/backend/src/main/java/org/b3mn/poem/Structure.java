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
		
		//Persistance.commit();
		return structure;
	}
	
	
	
}
	
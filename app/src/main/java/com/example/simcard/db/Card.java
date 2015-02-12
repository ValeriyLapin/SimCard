package com.example.simcard.db;

import java.io.Serializable;

public class Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// <Sn>1</Sn>
	// <NominalId>156</NominalId>

	public String Sn;
	Nominal nominal = null;
	// if -1 that it's startPocket

	// a temporary variable for listView
	public boolean isSelected = false;

	// for relation in database with salesOrder
//	public String saleOrderUid;

	public Card(String sn, Nominal nominal) {
		super();
		Sn = sn;
		this.nominal = nominal;
	}

	public int getNominalId() {
		if (nominal == null) {
			return -1;
		} else {
			return nominal.id;
		}

	}

	public Card(String sn) {
		super();
		Sn = sn;
		nominal = null;
	}

	@Override
	public String toString() {
		if (nominal != null) {
			return "(" + nominal.name + ") " + Sn;
		} else {
			return "" + Sn;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Sn == null) ? 0 : Sn.hashCode());
		result = prime * result + (isSelected ? 1231 : 1237);
		result = prime * result + ((nominal == null) ? 0 : nominal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (Sn == null) {
			if (other.Sn != null)
				return false;
		} else if (!Sn.equals(other.Sn))
			return false;
		if (isSelected != other.isSelected)
			return false;
		if (nominal == null) {
			if (other.nominal != null)
				return false;
		} else if (!nominal.equals(other.nominal))
			return false;
		return true;
	}

	

	
}

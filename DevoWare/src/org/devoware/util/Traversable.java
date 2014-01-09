package org.devoware.util;

public interface Traversable <T> {
	
	public void traverse (Visitor<T> visitor);

}

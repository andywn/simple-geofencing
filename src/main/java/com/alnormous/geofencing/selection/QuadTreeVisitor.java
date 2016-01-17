package com.alnormous.geofencing.selection;

import com.alnormous.geofencing.entities.Fence;

public interface QuadTreeVisitor {
	
	public void visit(Node node);

}

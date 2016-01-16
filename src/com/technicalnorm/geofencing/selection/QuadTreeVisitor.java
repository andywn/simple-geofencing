package com.technicalnorm.geofencing.selection;

import com.technicalnorm.geofencing.entities.Fence;

public interface QuadTreeVisitor {
	
	public void visit(Node node);

}

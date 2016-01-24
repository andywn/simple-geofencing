package com.alnormous.geofencing.selection;

import java.util.Iterator;

import com.alnormous.geofencing.entities.Fence;

public class QuadTreeVisitorImpl implements QuadTreeVisitor {

	private Fence fence;
	
	private static int MAX_DEPTH = 3;
	
	public QuadTreeVisitorImpl(Fence fence) {
		this.fence = fence;
	}
	
	@Override
	public void visit(Node node) throws OverlappingFencesException {
		// Node fit inside the fence?
		if (fence.contains(node.getEnvelope())) {
			// We can stop here.
			node.getFences().add(fence);
			node.setSoleFenceOccupancy(true);
			return;
		}
		// If there are no points in common, we're done here.
		if (!fence.overlapsOrContains(node.getEnvelope())) {
			return;
		}
		if (node.isSoleFenceOccupancy()) {
			// We're in trouble.
			throw new OverlappingFencesException("Fence " + fence.toString() + " overlaps another fence");
		}
		if (node.getDepth() >= MAX_DEPTH) {
			node.getFences().add(fence);
			return;
		}
		
		node.splitQuadrant();
		
		Iterator<Node> iterator = node.getIterator();
		while (iterator.hasNext()) {
			iterator.next().accept(this);
		}
		
	}

}

package com.alnormous.geofencing.selection;

import java.util.Iterator;

import com.alnormous.geofencing.entities.Fence;

public class QuadTreeVisitorImpl implements QuadTreeVisitor {

	private Fence fence;
	
	private int maxDepth;
	
	public QuadTreeVisitorImpl(Fence fence) {
		this.maxDepth = 6; // Default
		this.fence = fence;
	}
	
	public QuadTreeVisitorImpl(Fence fence, int depth) {
		this.fence = fence;
		this.maxDepth = depth;
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
			if (!fence.getId().equals(node.getFences().iterator().next().getId())) {
				// Fence overlap exists.
				throw new OverlappingFencesException("Fence " + fence.toString() + " overlaps another fence");
			}
		}
		if (node.getDepth() >= maxDepth) {
			node.getFences().add(fence);
			return;
		}
		
		// Generate four quadrants under this node.
		node.splitQuadrant();
		
		Iterator<Node> iterator = node.getIterator();
		while (iterator.hasNext()) {
			iterator.next().accept(this);
		}
		
	}

}

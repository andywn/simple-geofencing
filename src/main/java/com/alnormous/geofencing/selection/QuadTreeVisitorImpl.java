package com.alnormous.geofencing.selection;

import java.util.Iterator;

import com.alnormous.geofencing.entities.Fence;

public class QuadTreeVisitorImpl implements QuadTreeVisitor {

	private Fence fence;
	
	private static int MAX_DEPTH = 10;
	
	public QuadTreeVisitorImpl(Fence fence) {
		this.fence = fence;
	}
	
	@Override
	public void visit(Node node) {
		// Node fit inside the fence?
		if (fence.contains(node.getEnvelope())) {
			// We can stop here.
			node.getFences().add(fence);
			return;
		}
		// If there are no points in common, we're done here.
		if (!fence.overlap(node.getEnvelope())) {
			return;
		}
		if (node.getDepth() == MAX_DEPTH) {
			node.getFences().add(fence);
		}
		
		node.splitQuadrant();
		
		Iterator<Node> iterator = node.getIterator();
		while (iterator.hasNext()) {
			iterator.next().accept(this);
		}
		
	}

}

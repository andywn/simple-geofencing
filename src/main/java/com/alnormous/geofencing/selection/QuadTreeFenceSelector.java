package com.alnormous.geofencing.selection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.entities.Quadrant;
import com.vividsolutions.jts.geom.Envelope;

public class QuadTreeFenceSelector implements FenceSelector {
	
	private Set<Fence> fences;
	private Node node;
	
	/**
	 * For each fence:
	 * <ol>
	 * <li>Is any part of this quadrant in the fence?  To find out, first check the points inside polygon count</li>
	 * <li></li>
	 * <li>If so, do any of the lines between points cross the quad square?</li>
	 * <li>If not, then this whole quad belongs to the fence.  Assign.</li>
	 * </ol>
	 * 
	 * @param fences
	 * @param boundaryX
	 * @param boundaryY
	 */
	public QuadTreeFenceSelector(Set<Fence> fences, Coordinate upperLeft, Coordinate lowerRight, int depth) {
		node = new Node(upperLeft, lowerRight, 1);
		this.fences = fences;
		for (Fence f: fences) {
			if (depth > 0) {
				node.accept(new QuadTreeVisitorImpl(f, depth));
			} else {
				node.accept(new QuadTreeVisitorImpl(f));
			}
			
		}
		// Clean up fences.
		node.accept(new QuadTreePostProcessorVisitor());
	}
	
	public QuadTreeFenceSelector(Set<Fence> fences, Coordinate upperLeft, Coordinate lowerRight) {
		this(fences, upperLeft, lowerRight, -1);
	}

	@Override
	public Optional<Fence> selectFence(Coordinate coord) {
		Set<Fence> fences = node.getFence(coord);
		if (fences == null || fences.size() == 0) {
			return Optional.empty();
		} else if (fences.size() == 1) {
			if (coord.isPointInFence(fences.iterator().next())) {
				return Optional.of(fences.iterator().next());
			} else {
				return Optional.empty();
			}
		} else {
			return fences.parallelStream().filter(coord::isPointInFence).findAny();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Root\n");
		result.append(node.toString());
		result.append("\n");
		return result.toString();
	}

	public Set<Fence> getFences() {
		return fences;
	}
	
	

}

class Node {
	// Boundary
	private Coordinate center;
	private Coordinate upperLeft;
	private Coordinate lowerRight;
	
	// Only one fence occupies this node.
	private boolean soleFenceOccupancy = false;
	
	private Map<Quadrant, Node> nodes = new HashMap<>();
	private Set<Fence> fences = new HashSet<>();
	private Envelope envelope;
	private int depth;
	
	public Node(Coordinate upperLeftBoundary, Coordinate lowerRightBoundary, int depth) {
		envelope = new Envelope(upperLeftBoundary, lowerRightBoundary);
		center = upperLeftBoundary.getCenter(lowerRightBoundary);
		this.depth = depth;
		this.upperLeft = upperLeftBoundary;
		this.lowerRight = lowerRightBoundary;
	}
	
	public void accept(QuadTreeVisitor visitor) {
		try {
			visitor.visit(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Optional<Node> getNode(Quadrant quadrant) {
		return Optional.ofNullable(nodes.get(quadrant));
	}
	
	public Iterator<Node> getIterator() {
		return nodes.values().iterator();
	}
	
	
	public Set<Fence> getFence(Coordinate coord) {
		if (!envelope.contains(coord)) {
			return null;
		}
		if (nodes.get(coord.directionFrom(center)) == null) {
			return fences;
		} else {
			return nodes.get(coord.directionFrom(center)).getFence(coord);
		}
	}
	
	public void splitQuadrant() {
		if (!nodes.containsKey(Quadrant.ABOVE_LEFT)) {
			nodes.put(Quadrant.ABOVE_LEFT, new Node(this.upperLeft, this.center, depth+1));
		}
		if (!nodes.containsKey(Quadrant.BELOW_RIGHT)) {
			nodes.put(Quadrant.BELOW_RIGHT, new Node(this.center, this.lowerRight, depth+1));
		}
		if (!nodes.containsKey(Quadrant.ABOVE_RIGHT)) {
			Coordinate upperMid = new Coordinate(this.center.getX(), this.upperLeft.getY());
			Coordinate centerRight = new Coordinate(this.lowerRight.getX(), this.center.getY());
			nodes.put(Quadrant.ABOVE_RIGHT, new Node(upperMid, centerRight, depth+1));
		}
		if (!nodes.containsKey(Quadrant.BELOW_LEFT)) {
			Coordinate centerLeft = new Coordinate(this.upperLeft.getX(), this.center.getY());
			Coordinate lowerMid = new Coordinate(this.center.getX(), this.lowerRight.getY());
			nodes.put(Quadrant.BELOW_LEFT, new Node(centerLeft, lowerMid, depth+1));
		}
	}
	
	public Set<Fence> getFences() {
		return fences;
	}
	
	public void resetFences() {
		this.fences = new HashSet<>();
	}
	
	public Envelope getEnvelope() {
		return envelope;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public boolean isSoleFenceOccupancy() {
		return soleFenceOccupancy;
	}

	public void setSoleFenceOccupancy(boolean soleFenceOccupancy) {
		this.soleFenceOccupancy = soleFenceOccupancy;
		if (soleFenceOccupancy == true) {
			this.nodes = new HashMap<>(); // Clear children if this is a sole occupancy node.
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Node depth: ");
		result.append(this.getDepth());
		result.append("\n");
		result.append("Fences: ");
		for (Fence f: fences) {
			result.append(f.getName());
			result.append(",");
		}
		for (Quadrant q: nodes.keySet()) {
			result.append(q.toString());
			result.append(": ");
			result.append(nodes.get(q).toString());
			result.append("\n");
		}
		return result.toString();
	}
	
}

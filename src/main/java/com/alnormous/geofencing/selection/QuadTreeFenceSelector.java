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
	public QuadTreeFenceSelector(Set<Fence> fences, Coordinate upperLeft, Coordinate lowerRight) {
		node = new Node(upperLeft, lowerRight, 1);
		this.fences = fences;
		for (Fence f: fences) {
			node.accept(new QuadTreeVisitorImpl(f));
		}
	}

	@Override
	public Optional<Fence> selectFence(Coordinate coord) {
		Set<Fence> fences = node.getFence(coord);
		if (fences.size() == 0) {
			return Optional.empty();
		} else if (fences.size() == 1) {
			return Optional.of(fences.iterator().next());
		} else {
			return fences.parallelStream().filter(coord::isPointInFence).findAny();
		}
	}

}

class Node {
	// Boundary
	private Coordinate center;
	private Coordinate upperLeft;
	private Coordinate lowerRight;
	
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
		visitor.visit(this);
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
		nodes.put(Quadrant.ABOVE_LEFT, new Node(this.upperLeft, this.center, depth+1));
		nodes.put(Quadrant.BELOW_RIGHT, new Node(this.center, this.lowerRight, depth+1));
		Coordinate upperMid = new Coordinate(this.center.getX(), this.upperLeft.getY());
		Coordinate centerRight = new Coordinate(this.lowerRight.getX(), this.center.getY());
		nodes.put(Quadrant.ABOVE_RIGHT, new Node(upperMid, centerRight, depth+1));
		Coordinate centerLeft = new Coordinate(this.upperLeft.getX(), this.center.getY());
		Coordinate lowerMid = new Coordinate(this.center.getX(), this.lowerRight.getY());
		nodes.put(Quadrant.BELOW_LEFT, new Node(centerLeft, lowerMid, depth+1));
	}
	
	public Set<Fence> getFences() {
		return fences;
	}
	
	public Envelope getEnvelope() {
		return envelope;
	}
	
	public int getDepth() {
		return depth;
	}
	
}

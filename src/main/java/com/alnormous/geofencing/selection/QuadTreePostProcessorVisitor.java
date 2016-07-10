package com.alnormous.geofencing.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.entities.FenceValidationException;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Some ESRI files contain multiple polygons with the same ID.
 * This visitor cleans up Quad Tree nodes that contain multiple polygons of the same ID that
 * take up the entire envelope, and then cleans up the tree by removing nodes that aren't
 * required.
 * 
 * @author andrew
 *
 */
public class QuadTreePostProcessorVisitor implements QuadTreeVisitor {
	
	private static final double acceptableErrorMargin = 0.0001;

	@Override
	public void visit(Node node) throws Exception {
		if (node != null) {
			// DFS pre-visit
			if (!node.isSoleFenceOccupancy() && node.getFences() != null) {
				// First, break up fences by fence id
				Map<String, List<Fence>> fences = node.getFences().stream().collect(Collectors.groupingBy(f -> (f.getId() == null)?"null":f.getId()));
				if (fences.keySet().size() == 1) { // Candidate for merging
					// Calculate sum of area inside node envelope
					double area = fences.get(fences.keySet().iterator().next()).stream()
						.mapToDouble(f -> f.overlapArea(node.getEnvelope()))
						.sum();
					if (Math.abs(area - node.getEnvelope().getArea()) < acceptableErrorMargin) {
						node.setSoleFenceOccupancy(true);
						Fence randomFence = node.getFences().iterator().next();
						String name = randomFence.getName();
						String id = randomFence.getId();
						node.resetFences();
						node.getFences().add(buildFenceFromEnvelope(node.getEnvelope(), name, id));
					} else {
						System.out.println(Math.abs(area - node.getEnvelope().getArea()));
					}
				}
			}
			// Visit children
			Iterator<Node> iterator = node.getIterator();
			while (iterator.hasNext()) {
				iterator.next().accept(this);
			}
			// DFS post-visit
			String id = null;
			String name = null;
			boolean allSoleOccupancy = true;
			boolean allSameId = true;
			while (iterator.hasNext()) {
				Node child = iterator.next();
				if (child.isSoleFenceOccupancy()) {
					Fence soleFence = child.getFences().iterator().next();
					if (soleFence.getId() != null && (id == null || id.equals(soleFence.getId()))) {
						id = soleFence.getId();
						name = soleFence.getName();
					} else {
						allSameId = false;
						break;
					}
				} else {
					allSoleOccupancy = false;
					break;
				}
			}
			if (id != null && allSameId && allSoleOccupancy) {
				// All four children are sole occupancy envelopes, with the same ID.  Remove them, replace with single node.
				node.setSoleFenceOccupancy(true);
				node.resetFences();
				node.getFences().add(buildFenceFromEnvelope(node.getEnvelope(), name, id));
			}
		}
	}
	
	private Fence buildFenceFromEnvelope(Envelope e, String id, String name) throws FenceValidationException {
		List<Coordinate> coordinates = new ArrayList<>();
		coordinates.add(new Coordinate(e.getMinX(), e.getMaxY()));
		coordinates.add(new Coordinate(e.getMaxX(), e.getMaxY()));
		coordinates.add(new Coordinate(e.getMaxX(), e.getMinY()));
		coordinates.add(new Coordinate(e.getMinX(), e.getMinY()));
		return new Fence(coordinates, name, id);
	}
}

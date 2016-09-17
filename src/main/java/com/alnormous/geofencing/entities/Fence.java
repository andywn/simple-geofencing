package com.alnormous.geofencing.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Fence {
	
	private Polygon polygon;
	private String name;
	private String id;
	private GeometryFactory factory;
	private Map<String, String> attributes = new HashMap<>();
	
	
	public Fence(List<Coordinate> coordinates, String name, String id) throws FenceValidationException {
		if (coordinates.size() < 3) {
			throw new FenceValidationException();
		}
		factory = JTSFactoryFinder.getGeometryFactory();
		
		if (!coordinates.get(0).equals(coordinates.get(coordinates.size()-1))) {
			coordinates.add(coordinates.get(0));
		}
		Coordinate[] coords = new Coordinate[coordinates.size()];
		int i = 0;
		for (Coordinate c: coordinates) {
			coords[i] = c;
			i++;
		}
		CoordinateSequence sequence = new CoordinateArraySequence(coords); 
		polygon = factory.createPolygon(sequence);
		
		this.name = name;
		this.id = id;
	}
	
	public boolean contains(Coordinate coord) {
		return polygon.contains(factory.createPoint(coord));
	}
	
	public boolean contains(Envelope envelope) {
		return polygon.contains(factory.toGeometry(envelope));
	}
	
	public double overlapArea(Envelope envelope) {
		try {
			return factory.toGeometry(envelope).intersection(polygon).getArea();
		} catch (TopologyException e) {
			return 0.0;
		}
	}
	
	
	public boolean overlapsOrContains(Envelope envelope) {
		if (contains(envelope)) {
			return true;
		} if (envelope.contains(polygon.getEnvelopeInternal())) {
			return true;
		}
		try {
			return polygon.overlaps(factory.toGeometry(envelope));
		} catch (TopologyException te) {
			return false;
		}
	}
	

	public Geometry getEnvelope() {
		return polygon.getEnvelope();
	}
	
	public Geometry getEnclosingCircle() {
		return polygon.getExteriorRing();
	}
	
	public Polygon getPolygon() {
		return polygon;
	}
	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAttribute(String attribute) {
		return (attributes != null) ? attributes.get(attribute) : null;
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String id, String val) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put(id, val);
	}

}

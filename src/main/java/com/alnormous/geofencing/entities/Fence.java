package com.alnormous.geofencing.entities;

import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Fence {
	
	private Polygon polygon;
	private String name;
	private String id;
	private GeometryFactory factory;
	
	
	public Fence(List<Coordinate> coordinates, String name) throws FenceValidationException {
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
	}
	
	public boolean contains(Coordinate coord) {
		return polygon.contains(factory.createPoint(coord));
	}
	
	public boolean contains(Envelope envelope) {
		return polygon.contains(factory.toGeometry(envelope));
	}
	
	
	public boolean overlap(Envelope envelope) {
		return polygon.overlaps(factory.toGeometry(envelope));
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
	
	

}

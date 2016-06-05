package com.alnormous.geofencing.ingest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.selection.QuadTreeFenceSelector;
import com.vividsolutions.jts.geom.MultiPolygon;

public class GeotoolsImport implements Ingest {

	@Override
	public Optional<QuadTreeFenceSelector> readFile(File input, Function<SimpleFeature, String> fenceIdGen) throws Exception {
		
		FileDataStore dataStore = FileDataStoreFinder.getDataStore(input);
        SimpleFeatureSource shapefileSource = dataStore
                .getFeatureSource();
        
        
        Set<Fence> fences = new HashSet<>();
        
        Coordinate topLeft = null;
        Coordinate bottomRight = null;
        
        SimpleFeatureCollection collection = shapefileSource.getFeatures();
        SimpleFeatureIterator iterator = collection.features();
        while (iterator.hasNext()) {
        	SimpleFeature feature = iterator.next();
        	List<com.alnormous.geofencing.entities.Coordinate> coordList = new ArrayList<>();
        	if (feature.getDefaultGeometry() instanceof MultiPolygon) {
        		MultiPolygon p = (MultiPolygon) feature.getDefaultGeometry();
        		for (com.vividsolutions.jts.geom.Coordinate c: p.getCoordinates()) {
        			Coordinate coord = new Coordinate(c.x, c.y);
        			coordList.add(coord);
        			// check boundaries
        			if (topLeft == null && bottomRight == null) {
        				topLeft = new Coordinate(c.x,  c.y);
        				bottomRight = new Coordinate(c.x,  c.y);
        			} else {
        				if (c.x < topLeft.x) {
        					topLeft.x = c.x;
        				}
        				if (c.y > topLeft.y) {
        					topLeft.y = c.y;
        				}
        				if (c.x > bottomRight.x) {
        					bottomRight.x = c.x;
        				}
        				if (c.y < bottomRight.y) {
        					bottomRight.y = c.y;
        				}
        			}
        		}
        		fences.add(new Fence(coordList, fenceIdGen.apply(feature)));
        	}
        	
        }
        if (fences.size() == 0) {
        	return Optional.empty();
        }
        // TODO: Calculate actual top left, bottom right co-ordinates.
        QuadTreeFenceSelector selector = new QuadTreeFenceSelector(fences, topLeft, bottomRight);
        
        return Optional.of(selector);
	}

}

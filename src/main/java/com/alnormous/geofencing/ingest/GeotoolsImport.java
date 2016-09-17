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
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.selection.FenceSelector;
import com.alnormous.geofencing.selection.QuadTreeFenceSelector;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class GeotoolsImport implements Ingest {
	
	private Set<Fence> fences;
	private Coordinate topLeft, bottomRight;
	private int depth;
	
	private final String defaultTargetCRS = "EPSG:4140";
	
	private ImportFiles lastFileImport = null;
	
	public GeotoolsImport() {
		fences = new HashSet<>();
		topLeft = null;
		bottomRight = null;
	}
	
	@Override
	public GeotoolsImport readFile(String attribute, File... input) throws Exception {
		return readFile((f) -> (String)f.getAttribute(attribute), (f) -> (String)f.getAttribute(attribute), input);
	}
	
	public GeotoolsImport readFile(Function<SimpleFeature, String> fenceIdGen, Function<SimpleFeature, String> fenceNameGen, File... inputFiles) throws Exception {
		if (lastFileImport != null) {
			performFileIngest(lastFileImport);
		}
		lastFileImport = new ImportFiles(inputFiles, fenceIdGen, fenceNameGen);
		return this;
	}
	
	public GeotoolsImport setCRSSource(String crs) {
		if (lastFileImport != null) {
			lastFileImport.crsSource = crs;
		}
		return this;
	}
	
	public GeotoolsImport setCRSTarget(String crs) {
		if (lastFileImport != null) {
			lastFileImport.crsTarget = crs;
		}
		return this;
	}
	
	public GeotoolsImport transformToDefault() {
		if (lastFileImport != null) {
			lastFileImport.crsTarget = defaultTargetCRS;
		}
		return this;
	}
	
	private GeotoolsImport performFileIngest(ImportFiles files) throws Exception {
		Function<SimpleFeature, String> fenceIdGen = files.fenceIdGen;
		Function<SimpleFeature, String> fenceNameGen = files.fenceNameGen;
		File[] inputFiles = files.files;
		for (File input: inputFiles) {
			FileDataStore dataStore = FileDataStoreFinder.getDataStore(input);
			
			MathTransform transform = null;
			if (files.crsSource != null || files.crsTarget != null) {
				CoordinateReferenceSystem source = files.crsSource != null ? CRS.decode(files.crsSource) : dataStore.getSchema().getCoordinateReferenceSystem();
				CoordinateReferenceSystem target = files.crsTarget != null ? CRS.decode(files.crsTarget) : CRS.decode(defaultTargetCRS);
				transform = CRS.findMathTransform(source, target);
			}
			
	        SimpleFeatureSource shapefileSource = dataStore
	                .getFeatureSource();
	        
	        SimpleFeatureCollection collection = shapefileSource.getFeatures();
	        SimpleFeatureIterator iterator = collection.features();
	        while (iterator.hasNext()) {
	        	SimpleFeature feature = iterator.next();
	        	List<com.alnormous.geofencing.entities.Coordinate> coordList = new ArrayList<>();
	        	if (feature.getDefaultGeometry() instanceof MultiPolygon) {
	        		MultiPolygon p = (MultiPolygon) feature.getDefaultGeometry();
	        		if (transform != null) {
	        			p = (MultiPolygon)JTS.transform((Geometry)p, transform);
	        		}
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
	        		Fence f = new Fence(coordList, fenceNameGen.apply(feature), fenceIdGen.apply(feature));
	        		if (files.attributes != null) {
	        			for (String id: files.attributes) {
	        				f.addAttribute(id, (String)feature.getAttribute(id));
	        			}
	        		}
	        		fences.add(f);
	        	}
	        }
	        iterator.close();
	        dataStore.getFeatureReader().close();
	        dataStore.dispose();
		}
		return this;
	}
	
	public GeotoolsImport setTreeDepth(int depth) {
		this.depth = depth;
		return this;
	}
	
	@Override
	public Optional<FenceSelector> build() throws Exception {
		if (lastFileImport != null) {
			performFileIngest(lastFileImport);
		}
		if (fences == null || fences.size() == 0 || topLeft == null || bottomRight == null) {
        	return Optional.empty();
        }
		QuadTreeFenceSelector selector;
        if (depth > 0) {
        	selector = new QuadTreeFenceSelector(fences, topLeft, bottomRight, depth);
        } else {
        	selector = new QuadTreeFenceSelector(fences, topLeft, bottomRight);
        }
        return Optional.of(selector);
	}

	@Override
	public Ingest addAttributes(String... attributes) throws Exception {
		lastFileImport.attributes = attributes;
		return this;
	}
	
}

class ImportFiles {
	File[] files;
	Function<SimpleFeature, String> fenceIdGen; 
	Function<SimpleFeature, String> fenceNameGen;
	String crsSource;
	String crsTarget;
	String[] attributes;
	
	public ImportFiles(File[] files, Function<SimpleFeature, String> fenceIdGen,
			Function<SimpleFeature, String> fenceNameGen) {
		super();
		this.files = files;
		this.fenceIdGen = fenceIdGen;
		this.fenceNameGen = fenceNameGen;
	}
}

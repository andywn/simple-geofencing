package com.alnormous.geofencing.ingest;

import java.io.File;
import java.util.function.Function;

import org.opengis.feature.simple.SimpleFeature;

import com.alnormous.geofencing.selection.QuadTreeFenceSelector;

public interface Ingest {
	
	public QuadTreeFenceSelector readFile(File input, Function<SimpleFeature, String> fenceIdGen) throws Exception;

}

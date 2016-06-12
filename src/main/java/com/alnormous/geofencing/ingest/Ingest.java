package com.alnormous.geofencing.ingest;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

import org.opengis.feature.simple.SimpleFeature;

import com.alnormous.geofencing.selection.QuadTreeFenceSelector;

public interface Ingest {
	
	public Optional<QuadTreeFenceSelector> readFile(Function<SimpleFeature, String> fenceIdGen, File... input) throws Exception;

}

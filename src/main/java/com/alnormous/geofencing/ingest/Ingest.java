package com.alnormous.geofencing.ingest;

import java.io.File;
import java.util.Optional;

import com.alnormous.geofencing.selection.FenceSelector;

public interface Ingest {
	
	public Ingest readFile(String attribute, File... input) throws Exception;
	public Optional<FenceSelector> build();

}

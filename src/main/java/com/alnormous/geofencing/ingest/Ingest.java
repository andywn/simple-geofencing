package com.alnormous.geofencing.ingest;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.selection.FenceSelector;

public interface Ingest {
	
	public Ingest readFile(String attribute, File... input) throws Exception;
	public Ingest addAttributes(String... attributes) throws Exception;
	public Optional<FenceSelector> build() throws Exception;
	public void setOnCreation(Consumer<Fence> onCreation);

}

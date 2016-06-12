package com.alnormous.geofencing.test.ingest;

import java.io.File;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.ingest.GeotoolsImport;
import com.alnormous.geofencing.selection.QuadTreeFenceSelector;

/**
 * This test requires the NSW electoral map shape files to run.
 * For copy right reasons, the file is not included here.
 * 
 * @author andrew
 *
 */
public class GeotoolsImportTest {
	
	@Test
	public void test() throws Exception {
		
		File file = new File("src/test/resources/NSW_SA1s_25-02-2016.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
        QuadTreeFenceSelector selector = geoImport.readFile((f) -> (String)f.getAttribute("elect_div"), file).get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(145.0,  -35.0));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Farrer", fence.get().getName());
		
	}

}

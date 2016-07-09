package com.alnormous.geofencing.test.ingest;

import java.io.File;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.ingest.GeotoolsImport;
import com.alnormous.geofencing.selection.FenceSelector;

/**
 * This test requires the electoral map shape files to run.
 * For copy right reasons, the files are not included here.
 * You can download them at http://data.gov.au
 * 
 * @author andrew
 *
 */
public class GeotoolsImportTest {
	
	@Test
	public void testNSW() throws Exception {
		
		File file = new File("src/test/resources/NSW_SA1s_25-02-2016.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
        FenceSelector selector = geoImport.readFile((f) -> (String)f.getAttribute("elect_div"),
        		(f) -> (String)f.getAttribute("elect_div"), file).build().get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(145.0,  -35.0));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Farrer", fence.get().getName());
		
	}
	
	@Test
	public void testACT() throws Exception {
		
		File file = new File("src/test/resources/ACT_SA1s_28-01-2016.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
		FenceSelector selector = geoImport.readFile((f) -> (String)f.getAttribute("elect_div"), 
				(f) -> (String)f.getAttribute("elect_div"), file).build().get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(149.1249668, -35.275248));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Canberra", fence.get().getName());
		
	}
	
	@Test
	public void testQLD() throws Exception {
		
		File file = new File("src/test/resources/QLD_ELB_031209_region.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
		FenceSelector selector = geoImport.readFile((f) -> (String)f.getAttribute("ELECT_DIV"), 
				(f) -> (String)f.getAttribute("ELECT_DIV"), file).build().get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(144.5695493,-24.5663331));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Maranoa", fence.get().getName());
		
	}
	
	public void testVIC() throws Exception {
		
		File file = new File("src/test/resources/vic24122010.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
		FenceSelector selector = geoImport.readFile((f) -> (String)f.getAttribute("ELECT_DIV"), 
				(f) -> (String)f.getAttribute("ELECT_DIV"), file).build().get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(144.7729576,-37.9716929));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Farrer", fence.get().getName());
		
	}
	
	@Test
	public void testWA() throws Exception {
		
		File file = new File("src/test/resources/WA_SA1s_19-01-2016.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
		FenceSelector selector = geoImport.readFile((f) -> (String)f.getAttribute("elect_div"), 
				(f) -> (String)f.getAttribute("elect_div"), file).build().get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(115.9881563, -31.909283));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Hasluck", fence.get().getName());
		
	}
	
	@Test
	public void testSA() throws Exception {
		
		File file = new File("src/test/resources/E_SA16122011_region.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
		FenceSelector selector = geoImport.readFile((f) -> (String)f.getAttribute("ELECT_DIV"), 
				(f) -> (String)f.getAttribute("ELECT_DIV"), file).build().get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(137.728285, -32.4903316));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Grey", fence.get().getName());
		
	}

	
	@Test
	public void testWithStringAttribute() throws Exception {
		
		File file = new File("src/test/resources/NSW_SA1s_25-02-2016.shp");
		
		GeotoolsImport geoImport = new GeotoolsImport();
		
        FenceSelector selector = geoImport.readFile("elect_div", file).build().get();
        
        Optional<Fence> fence = selector.selectFence(new com.alnormous.geofencing.entities.Coordinate(145.0,  -35.0));
        fence.ifPresent((f) -> System.out.println(f.getName()));
        
        Assert.assertTrue(fence.isPresent());
        Assert.assertEquals("Farrer", fence.get().getName());
		
	}

}

package com.example.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.BitSet;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.code.CACodes;

import gsearch.GSearch;
import gsearch.ParamBitSet;
import gsearch.exchange.Exchange;

public class CAAppTest {
	Logger logger = Logger.getLogger(CAAppTest.class.getName());

	@BeforeClass
	public static void testSetup() {
	}

	@AfterClass
	public static void testCleanup() {
	  // Teardown for data used by the unit tests
	}

	@Test
    public void testApp() throws Exception
    {
//    	return;
    	
    	File index = new File(CAAppTest.class.getResource("/index").toURI() );
    	File indextaxo = new File( CAAppTest.class.getResource("/indextaxo").toURI() );
    	File xmlcodes = new File( CAAppTest.class.getResource("/xmlcodes").toURI() );
    	
        GSearch gSearch = new GSearch( new CACodes(), xmlcodes, index, indextaxo  );
    	
    	// initial state
    	Exchange exchange = gSearch.handleRequest(null, null, null, false);
    	printExchange(exchange);
    	
       	logger.fine( "Group 0" );    
//        assertEquals( exchange.state, 1 );
       	ParamBitSet codesSelected = new ParamBitSet(exchange.codesAvailable.size());
       	codesSelected.set(1);
    	exchange = gSearch.handleRequest( codesSelected.toString(), null, null, false );
       	printExchange(exchange);

       	logger.fine( "Group 1" );    
//        assertEquals( exchange.state, 2 );
        assertEquals( exchange.path, null);
        assertEquals( exchange.term, null );
        assertEquals( exchange.selectedCodesList.size(), 1 );
        assertEquals( exchange.selectedCodesList.get(0).count, 0 );
        assertEquals( exchange.pathList, null );
        assertEquals( exchange.subcodeList, null );
        assertEquals( exchange.sectionTextList, null );

        exchange = gSearch.handleRequest(null, "civil-0", null, false );
       	printExchange(exchange);

       	logger.fine( "Group 2" );    
//        assertEquals( exchange.state, 3 );
        assertEquals( exchange.path, "civil-0");
        assertEquals( exchange.term, null );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(), 1 );
        assertEquals( exchange.pathList.get(0).count, 0 );
        assertEquals( exchange.subcodeList.size(), 9 );
        assertEquals( exchange.subcodeList.get(0).count, 0 );
        assertEquals( exchange.sectionTextList, null );

    	exchange = gSearch.handleRequest(null, "civil-0|civil-1-0", null, false );
       	printExchange(exchange);

       	logger.fine( "Group 3" );    
//        assertEquals( exchange.state, 5 );
        assertEquals( exchange.path, "civil-0|civil-1-0" );
        assertEquals( exchange.term, null );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(), 2 );
        assertEquals( exchange.pathList.get(0).count, 0 );
        assertEquals( exchange.subcodeList, null );
        // if this below fails its an indication that the comparison test is GSearch.processTerm() is broken
        assertEquals( exchange.sectionTextList.size(), 1 );
        assertEquals( exchange.sectionTextList.get(0).text.length(), 314 );

    	exchange = gSearch.handleRequest( codesSelected.toString(), null, "tenant", false );
       	printExchange(exchange);

       	logger.fine( "Group 6" );    
//       	assertEquals( exchange.state, 2 );
        assertEquals( exchange.path, null );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList.size(), 1 );
        assertEquals( exchange.selectedCodesList.get(0).count, 169 );
        assertEquals( exchange.pathList, null );
        assertEquals( exchange.subcodeList, null );
        assertEquals( exchange.sectionTextList, null );

    	exchange = gSearch.handleRequest( null, "civil-0", "tenant", false );
       	printExchange(exchange);

       	logger.fine( "Group 7" );    
//       	assertEquals( exchange.state, 3 );
        assertEquals( exchange.path, "civil-0" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(), 1 );
        assertEquals( exchange.subcodeList.size(), 9 );
        assertEquals( exchange.subcodeList.get(6).count, 57 );
        assertEquals( exchange.sectionTextList, null );

    	exchange = gSearch.handleRequest( null, "civil-0|civil-1-6", "tenant", false ); 
       	printExchange(exchange);

       	logger.fine( "Group 8" );    
//        assertEquals( exchange.state, 4 );
        assertEquals( exchange.path, "civil-0|civil-1-6" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(), 2 );
        assertEquals( exchange.subcodeList.size(), 4 );
        assertEquals( exchange.subcodeList.get(1).count, 36 );
        assertEquals( exchange.sectionTextList, null );

    	exchange = gSearch.handleRequest(null, "civil-0|civil-1-6|civil-2-1", "tenant", false );
       	printExchange(exchange);

       	logger.fine( "Group 9" );    
//        assertEquals( exchange.state , 4 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(),  3);
        assertEquals( exchange.subcodeList.size(), 6 );
        assertEquals( exchange.subcodeList.get(2).count , 6 );
        assertEquals( exchange.sectionTextList, null );

    	exchange = gSearch.handleRequest(null, "civil-0|civil-1-6|civil-2-1|civil-3-2", "tenant", false );
       	printExchange(exchange);

       	logger.fine( "Group 10" );    
//        assertEquals( exchange.state , 4 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1|civil-3-2" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(),  4);
        assertEquals( exchange.subcodeList.size(), 3 );
        assertEquals( exchange.subcodeList.get(1).count , 3 );
        assertEquals( exchange.sectionTextList, null );

    	exchange = gSearch.handleRequest( null, "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1", "tenant", false );

       	printExchange(exchange);

       	logger.fine( "Group 11" );    
//        assertEquals( exchange.state , 5 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(),  5);
        assertEquals( exchange.subcodeList, null );
        // if this below fails its an indication that the comparison test is GSearch.processTerm() is broken
        assertEquals( exchange.sectionTextList.size(), 11 );
        assertEquals( exchange.sectionTextList.get(3).text.length() , 1868 );

    	exchange = gSearch.handleRequest( null, "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1", "\"responsibility of the owner\"", false );
       	printExchange(exchange);

       	logger.fine( "Group 12" );    
//        assertEquals( exchange.state , 5 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1" );
        assertEquals( exchange.term, "\"responsibility of the owner\"" );
        assertEquals( exchange.selectedCodesList, null );
        assertEquals( exchange.pathList.size(),  5);
        assertEquals( exchange.subcodeList, null );
        // if this below fails its an indication that the comparison test is GSearch.processTerm() is broken
        assertEquals( exchange.sectionTextList.size(), 11 );
        // This is testing that the "No Terms Found." is appearing.
        assertEquals( exchange.sectionTextList.get(0).text.length() , 20 );
        assertEquals( exchange.sectionTextList.get(8).text.length() , 1130 );

        gSearch.destroy();

    }

    private void printExchange(Exchange exchange) {
    	
    	logger.fine( "Path = " + exchange.path + ": Term = " + exchange.term + "\n" + 
			"selectedCodesList = " + exchange.selectedCodesList + "\n" + 
			"pathList = " + exchange.pathList + "\n" + 
			"subcodeList = " + exchange.subcodeList + "\n" + 
			"sectionTextList = " + exchange.sectionTextList + "\n" ); 
    	    	
    }

}


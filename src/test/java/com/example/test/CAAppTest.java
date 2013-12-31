package com.example.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.code.CACodes;

import exchange.Exchange;
import scsb.SCSB;

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
    	
        SCSB scsb = new SCSB( new CACodes(), xmlcodes, index, indextaxo  );
    	
    	// initial state
    	Exchange exchange = scsb.getExchange( 1, "", "", false, false, false );
    	scsb.handleRequest(exchange);
    	printExchange(exchange);
    	
       	logger.fine( "Group 0" );    
        assertEquals( exchange.state, 1 );
        
    	exchange = scsb.getExchange( 1, "", "", false, true, false );
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 1" );    
        assertEquals( exchange.state, 2 );
        assertEquals( exchange.path.isEmpty(), true );
        assertEquals( exchange.term.isEmpty(), true );
        assertEquals( exchange.selectedCodesList.size(), 1 );
        assertEquals( exchange.selectedCodesList.get(0).count, 0 );
        assertEquals( exchange.pathList.size(), 0 );
        assertEquals( exchange.subcodeList.size(), 0 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange( 2, "", "civil-0", true, false, false ); 
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 2" );    
        assertEquals( exchange.state, 3 );
        assertEquals( exchange.path, "civil-0");
        assertEquals( exchange.term.isEmpty(), true );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(), 1 );
        assertEquals( exchange.pathList.get(0).count, 0 );
        assertEquals( exchange.subcodeList.size(), 9 );
        assertEquals( exchange.subcodeList.get(0).count, 0 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange(3, "", "civil-0|civil-1-0", true, false, false );
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 3" );    
        assertEquals( exchange.state, 5 );
        assertEquals( exchange.path, "civil-0|civil-1-0" );
        assertEquals( exchange.term.isEmpty(), true );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(), 2 );
        assertEquals( exchange.pathList.get(0).count, 0 );
        assertEquals( exchange.subcodeList.size(), 0 );
        // if this below fails its an indication that the comparison test is SCSB.processTerm() is broken
        assertEquals( exchange.sectionTextList.size(), 1 );
        assertEquals( exchange.sectionTextList.get(0).text.length(), 314 );

    	exchange = scsb.getExchange( 5, "", "civil-0|civil-1-0", false, true, false );
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 4" );    
        assertEquals( exchange.state, 1 );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(), 0 );
        assertEquals( exchange.subcodeList.size(), 0 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange( 1, "", "", false, true, false );
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 5" );    
       	assertEquals( exchange.state, 2 );
        assertEquals( exchange.path.isEmpty(), true );
        assertEquals( exchange.term.isEmpty(), true );
        assertEquals( exchange.selectedCodesList.size(), 1 );
        assertEquals( exchange.selectedCodesList.get(0).count, 0 );
        assertEquals( exchange.pathList.size(), 0 );
        assertEquals( exchange.subcodeList.size(), 0 );
        assertEquals( exchange.sectionTextList.size() , 0 );

    	exchange = scsb.getExchange( 2, "tenant", "", false, false, false );
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 6" );    
       	assertEquals( exchange.state, 2 );
        assertEquals( exchange.path.isEmpty(),true );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList.size(), 1 );
        assertEquals( exchange.selectedCodesList.get(0).count, 169 );
        assertEquals( exchange.pathList.size(), 0 );
        assertEquals( exchange.subcodeList.size(), 0 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange( 2, "tenant", "civil-0", true, false, false );
    	exchange.codesAvailable.get(1).selected = true;

    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 7" );    
       	assertEquals( exchange.state, 3 );
        assertEquals( exchange.path, "civil-0" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(), 1 );
        assertEquals( exchange.subcodeList.size(), 9 );
        assertEquals( exchange.subcodeList.get(6).count, 57 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange( 3, "tenant", "civil-0|civil-1-6", true, false, false ); 
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 8" );    
        assertEquals( exchange.state, 4 );
        assertEquals( exchange.path, "civil-0|civil-1-6" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(), 2 );
        assertEquals( exchange.subcodeList.size(), 4 );
        assertEquals( exchange.subcodeList.get(1).count, 36 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange(4, "tenant", "civil-0|civil-1-6|civil-2-1", true, false, false );
    	exchange.codesAvailable.get(1).selected = true;
    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 9" );    
        assertEquals( exchange.state , 4 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(),  3);
        assertEquals( exchange.subcodeList.size(), 6 );
        assertEquals( exchange.subcodeList.get(2).count , 6 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange(4, "tenant", "civil-0|civil-1-6|civil-2-1|civil-3-2", true, false, false );
    	exchange.codesAvailable.get(1).selected = true;

    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 10" );    
        assertEquals( exchange.state , 4 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1|civil-3-2" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(),  4);
        assertEquals( exchange.subcodeList.size(), 3 );
        assertEquals( exchange.subcodeList.get(1).count , 3 );
        assertEquals( exchange.sectionTextList.size(), 0 );

    	exchange = scsb.getExchange( 4, "tenant", "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1", true, false, false );
    	exchange.codesAvailable.get(1).selected = true;

    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 11" );    
        assertEquals( exchange.state , 5 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1" );
        assertEquals( exchange.term, "tenant" );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(),  5);
        assertEquals( exchange.subcodeList.size(), 0 );
        // if this below fails its an indication that the comparison test is SCSB.processTerm() is broken
        assertEquals( exchange.sectionTextList.size(), 11 );
        assertEquals( exchange.sectionTextList.get(3).text.length() , 1868 );

    	exchange = scsb.getExchange( 4, "\"responsibility of the owner\"", "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1", true, false, false );
    	exchange.codesAvailable.get(1).selected = true;

    	scsb.handleRequest(exchange);
       	printExchange(exchange);

       	logger.fine( "Group 12" );    
        assertEquals( exchange.state , 5 );
        assertEquals( exchange.path, "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1" );
        assertEquals( exchange.term, "\"responsibility of the owner\"" );
        assertEquals( exchange.selectedCodesList.size(), 0 );
        assertEquals( exchange.pathList.size(),  5);
        assertEquals( exchange.subcodeList.size(), 0 );
        // if this below fails its an indication that the comparison test is SCSB.processTerm() is broken
        assertEquals( exchange.sectionTextList.size(), 11 );
        // This is testing that the "No Terms Found." is appearing.
        assertEquals( exchange.sectionTextList.get(0).text.length() , 20 );
        assertEquals( exchange.sectionTextList.get(8).text.length() , 1130 );

        scsb.destroy();

    }

    private void printExchange(Exchange exchange) {
    	
    	logger.fine( "State = " + exchange.state + ": Browse = " + exchange.browse + ": Path = " + exchange.path + ": Term = " + exchange.term + "\n" + 
			"selectedCodesList = " + exchange.selectedCodesList + "\n" + 
			"pathList = " + exchange.pathList + "\n" + 
			"subcodeList = " + exchange.subcodeList + "\n" + 
			"sectionTextList = " + exchange.sectionTextList + "\n" ); 
    	    	
    }

}


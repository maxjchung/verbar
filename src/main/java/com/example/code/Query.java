package com.example.code;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import scsb.SCSB;
import exchange.Exchange;

public class Query {

	private static final Logger logger = Logger.getLogger(Query.class.getName());

	public static void main(String... args) throws Exception {
		File index = new File(Query.class.getResource("/index").toURI());
		File indextaxo = new File(Query.class.getResource("/indextaxo").toURI());
		File xmlcodes = new File(Query.class.getResource("/xmlcodes").toURI());

		SCSB scsb = new SCSB(new CACodes(), xmlcodes, index, indextaxo);

		// initial state
    	Exchange exchange = scsb.getExchange( 4, "\"responsibility of the owner\"", "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1", true, false, false );
    	exchange.codesAvailable.get(1).selected = true;

    	scsb.handleRequest(exchange);
    	Response response = Response.ok(exchange).build();
    	System.out.println( response.toString() );
		// XML Binding code using JAXB
//		JAXBContext jaxbCtx = JAXBContext.newInstance( Exchange.class );
//		StringWriter xmlWriter = new StringWriter();
//		jaxbCtx.createMarshaller().marshal(exchange, xmlWriter);
	}
	
	private static void printExchange(Exchange exchange) {

		logger.info("State = " + exchange.state + ": Browse = "
				+ exchange.browse + ": Path = " + exchange.path + ": Term = "
				+ exchange.term + "\n" + "selectedCodesList = "
				+ exchange.selectedCodesList + "\n" + "pathList = "
				+ exchange.pathList + "\n" + "subcodeList = "
				+ exchange.subcodeList + "\n" + "sectionTextList = "
				+ exchange.sectionTextList + "\n");

	}
}

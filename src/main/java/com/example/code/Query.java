package com.example.code;

import java.io.File;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;

import gsearch.GSearch;
import gsearch.exchange.Exchange;

public class Query {

	private static final Logger logger = Logger.getLogger(Query.class.getName());

	public static void main(String... args) throws Exception {
		File index = new File(Query.class.getResource("/index").toURI());
		File indextaxo = new File(Query.class.getResource("/indextaxo").toURI());
		File xmlcodes = new File(Query.class.getResource("/xmlcodes").toURI());

		GSearch gSearch = new GSearch(new CACodes(), xmlcodes, index, indextaxo);

//    	Exchange exchange = gSearch.getExchange( null, null, "civil-0|civil-1-6|civil-2-1|civil-3-2|civil-4-1", true, false, false );
		// initial state
    	Exchange exchange = gSearch.handleRequest(null, null, null, false);

    	//    	Response response = Response.ok(exchange).build();
 //   	System.out.println( response.toString() );
		// XML Binding code using JAXB
		JAXBContext jaxbCtx = JAXBContext.newInstance( Exchange.class );
		StringWriter xmlWriter = new StringWriter();
		jaxbCtx.createMarshaller().marshal(exchange, xmlWriter);
		System.out.println(xmlWriter.toString());
	}
	
	private static void printExchange(Exchange exchange) {

		logger.info("Path = " + exchange.path + ": Term = "
				+ exchange.term + "\n" + "selectedCodesList = "
				+ exchange.selectedCodesList + "\n" + "pathList = "
				+ exchange.pathList + "\n" + "subcodeList = "
				+ exchange.subcodeList + "\n" + "sectionTextList = "
				+ exchange.sectionTextList + "\n");

	}
}

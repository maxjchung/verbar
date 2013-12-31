package com.example.rest;

import java.io.File;
import java.util.ArrayList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import scsb.SCSB;

import com.example.code.CACodes;
import com.example.code.Query;

import exchange.CodeAvailable;
import exchange.Exchange;
import exchange.ListEntry;
import exchange.PathListEntry;
import exchange.TextEntry;

@Path("/")
public class GuidedLegal {
	
	private File index;
	private File indextaxo;
	private File xmlcodes;

	private SCSB scsb;

	public GuidedLegal() throws Exception {
		index = new File(Query.class.getResource("/index").toURI());
		indextaxo = new File(Query.class.getResource("/indextaxo").toURI());
		xmlcodes = new File(Query.class.getResource("/xmlcodes").toURI());
	
		scsb = new SCSB(new CACodes(), xmlcodes, index, indextaxo);
	}
	
	@POST
	@Path("search")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response post(Exchange exchange) throws Exception {

		if ( exchange.codesAvailable == null ) exchange.codesAvailable = new ArrayList<CodeAvailable>();
		if ( exchange.selectedCodesList == null ) exchange.selectedCodesList = new ArrayList<ListEntry>();
		if ( exchange.pathList == null ) exchange.pathList = new ArrayList<PathListEntry>();
		if ( exchange.subcodeList == null ) exchange.subcodeList = new ArrayList<ListEntry>();
		if ( exchange.sectionTextList == null ) exchange.sectionTextList = new ArrayList<TextEntry>();

		scsb.handleRequest(exchange);

    	return Response.ok().entity(exchange).build();
	}

	@GET
	@Path("newexchange")
	@Produces(MediaType.APPLICATION_XML)
	public Response get() throws Exception {
		// initial state
    	Exchange exchange = scsb.getExchange( 1, "", "", false, false, false );

    	return Response.ok().entity(exchange).build();
	}

}

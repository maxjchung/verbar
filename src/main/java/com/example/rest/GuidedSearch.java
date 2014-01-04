package com.example.rest;

import java.io.File;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.code.CACodes;
import com.example.code.Query;

import gsearch.exchange.*;
import gsearch.GSearch;

@Path("/search")
public class GuidedSearch {
	private static final Logger logger = Logger.getLogger(GuidedSearch.class.getName());
	
	private File index;
	private File indextaxo;
	private File xmlcodes;

	private GSearch gSearch;

	public GuidedSearch() throws Exception {
		index = new File(Query.class.getResource("/index").toURI());
		indextaxo = new File(Query.class.getResource("/indextaxo").toURI());
		xmlcodes = new File(Query.class.getResource("/xmlcodes").toURI());
	
		gSearch = new GSearch(new CACodes(), xmlcodes, index, indextaxo);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response get(
		@QueryParam("codeselect") String selectedcodes, 
		@QueryParam("path")String path,
		@QueryParam("term")String term, 
		@QueryParam("highlights")boolean highlights
	) throws Exception {

		if ( path != null && path.isEmpty()) path = null;
		if ( term != null && term.isEmpty()) term = null;

		logger.fine("get:" + selectedcodes + ":" + path + ":" + term + ":" + highlights );
		Exchange exchange = gSearch.handleRequest(selectedcodes, path, term, highlights);

		logger.fine("get after:" + exchange.codeselect + ":" + exchange.path + ":" + exchange.term + ":" + exchange.highlights );

		return Response.ok().entity(exchange).build();
	}

}

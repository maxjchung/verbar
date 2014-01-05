package com.example.rest;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.code.CACodes;
import com.example.code.Query;

import gsearch.exchange.*;
import gsearch.GSearch;

@Path("/search")
public class GuidedSearch extends Application {
	private static final Logger logger = Logger.getLogger(GuidedSearch.class.getName());
	
	private File index;
	private File indextaxo;
	private File xmlcodes;

	private GSearch gSearch;

    private Set<Object> singletons = new HashSet<Object>();
    public Set<Object> getSingletons() {return singletons;}

    public GuidedSearch() throws Exception {
        // ADD YOUR RESTFUL RESOURCES HERE
        singletons.add(this);

		index = new File(Query.class.getResource("/index").toURI());
		indextaxo = new File(Query.class.getResource("/indextaxo").toURI());
		xmlcodes = new File(Query.class.getResource("/xmlcodes").toURI());
	
		gSearch = new GSearch(new CACodes(), xmlcodes, index, indextaxo);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response get(
		@QueryParam("select")String select, 
		@QueryParam("path")String path,
		@QueryParam("term")String term, 
		@QueryParam("highlights")boolean highlights
	) throws Exception {

		if ( path != null && path.isEmpty()) path = null;
		if ( term != null && term.isEmpty()) term = null;

		logger.fine("get:" + select + ":" + path + ":" + term + ":" + highlights );
		Exchange exchange = gSearch.handleRequest(select, path, term, highlights);

		logger.fine("get after:" + ":" + exchange.path + ":" + exchange.term + ":" + exchange.highlights );

		return Response.ok().entity(exchange).build();
	}

}

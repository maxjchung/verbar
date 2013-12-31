package com.example.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.example.model.GSearchModel;

import exchange.CodeAvailable;
import exchange.Exchange;
import exchange.ListEntry;
import exchange.PathListEntry;
import exchange.TextEntry;

/**
 * Servlet implementation class Search
 * 
 */
public class GSearch extends HttpServlet {
	private static final Logger logger = Logger.getLogger(GSearch.class.getName());

	private static final long serialVersionUID = 1L;
	
	private Exchange exchange;
	private Client client;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public GSearch() {
        super();
    }

    @Override
    public void init() throws ServletException {
    	super.init();
		ServletContext context = getServletContext();
		synchronized(this) {
			client = (Client)context.getAttribute("client");
			if ( client == null ) {
				client = ClientBuilder.newBuilder().build();
		        context.setAttribute("client", client);
			}
		}
    }

    @Override
    public void destroy() {
    	super.destroy();
		client.close();
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/GSearch.jsp");
		dispatcher.forward(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/GSearch.jsp");
		dispatcher.forward(request, response);
	}
	
	private void process( HttpServletRequest request ) throws IOException {
		// This is the CONTROLLER logic
		if ( exchange == null ) {
			ServletContext context = getServletContext();
			synchronized(this) {
				exchange = (Exchange)context.getAttribute("exchange");
				if ( exchange == null ) {
					try {
				        WebTarget target = client.target("http://localhost:8080/rest/newexchange");
				        exchange = target.request(MediaType.APPLICATION_XML).get(Exchange.class);
						if ( exchange.codesAvailable == null ) exchange.codesAvailable = new ArrayList<CodeAvailable>();
						if ( exchange.selectedCodesList == null ) exchange.selectedCodesList = new ArrayList<ListEntry>();
						if ( exchange.pathList == null ) exchange.pathList = new ArrayList<PathListEntry>();
						if ( exchange.subcodeList == null ) exchange.subcodeList = new ArrayList<ListEntry>();
						if ( exchange.sectionTextList == null ) exchange.sectionTextList = new ArrayList<TextEntry>();
						
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
			        context.setAttribute("exchange", exchange);
				}
			}
		}

		// The purpose of this class is to retrieve all the parameters that
		// are stored in the view and put them into the exchange structure
		String currentstate;
		String currentterm;
		String currentpath;
		String newterm;
		String newpath;
		boolean browse;
		boolean selectall;
		boolean unselectall;
		boolean allselected;
		boolean highlights;
		boolean toggle;
		
		// retrieve all possible parameters from request		
		currentstate = request.getParameter("currentstate");
		if ( currentstate == null ) currentstate = "1";

		currentterm = request.getParameter("currentterm");
		if ( currentterm == null ) currentterm = "";

		currentpath = request.getParameter("currentpath");
		if ( currentpath == null ) currentpath = "";

		newpath = request.getParameter("newpath");
		if ( newpath == null ) newpath = "";
		
		newterm = request.getParameter("newterm");
		if ( newterm == null ) newterm = "";

		browse = request.getParameter("browse")==null?false:true;
		

		// select all logic is not part of SCSB
		// so deal with it here .. 
		// by defaults, all the exchange.codesAvailable are not selected, 
		// so selected any that need to be selected only if we need to
		selectall = request.getParameter("selectall")==null?false:true;
		unselectall = request.getParameter("unselectall")==null?false:true;
		allselected = request.getParameter("allselected")==null?false:true;

		String highString = request.getParameter("highlights");
		if ( highString == null ) highString = "";
		highlights = highString.compareTo("true")==0;
		toggle = request.getParameter("toggle")==null?false:true;

		GSearchModel model = new GSearchModel(
			exchange, 
			currentstate, 
			currentterm, 
			newterm, 
			currentpath, 
			newpath, 
			browse, 
			selectall,
			unselectall, 
			allselected, 
			highlights, 
			toggle
		);
		
		if ( selectall || (allselected && !unselectall) ) {
			for ( int i=0, l=model.exchange.codesAvailable.size(); i<l; ++i ) {
				model.exchange.codesAvailable.get(i).selected = true;
			}
		} else if ( !unselectall ) {
			// if all not selected, then copy the current state
			// but if the user clicked unselect all, then leave the array empty
			for ( int i=0, l=model.exchange.codesAvailable.size(); i<l; ++i ) {
				if ( request.getParameter(model.exchange.codesAvailable.get(i).fullFacet) != null ) {
					model.exchange.codesAvailable.get(i).selected = true;
				}			
			}
		}
		// process the requests with in the MODEL
		logger.fine("1: Browse = " + model.exchange.browse + ": State = " + model.exchange.state );
		logger.fine("1: Path = " + model.exchange.path + ": Term = " + model.exchange.term + ": isAllSelected = " + model.allSelected );
		// store the output for the VIEW
		
        WebTarget target = client.target("http://localhost:8080/rest/search");
        Entity<Exchange> entity = Entity.entity(model.exchange, MediaType.APPLICATION_XML);
        exchange = target.request(MediaType.APPLICATION_XML).post(entity, Exchange.class);
		//
		if ( exchange.codesAvailable == null ) exchange.codesAvailable = new ArrayList<CodeAvailable>();
		if ( exchange.selectedCodesList == null ) exchange.selectedCodesList = new ArrayList<ListEntry>();
		if ( exchange.pathList == null ) exchange.pathList = new ArrayList<PathListEntry>();
		if ( exchange.subcodeList == null ) exchange.subcodeList = new ArrayList<ListEntry>();
		if ( exchange.sectionTextList == null ) exchange.sectionTextList = new ArrayList<TextEntry>();

        model.exchange = exchange;
        
        request.setAttribute("model", model );

	}
	
}

package com.example.servlet;

import gsearch.exchange.CodeAvailable;
import gsearch.exchange.Exchange;
import gsearch.exchange.ListEntry;
import gsearch.exchange.PathListEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.model.GSearchModel;

/**
 * Servlet implementation class GSearch
 * 
 */
public class GSearch extends HttpServlet {
	private static final Logger logger = Logger.getLogger(GSearch.class.getName());

	private static final long serialVersionUID = 1L;
	
	private Client client;
	private String urlRoot;

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
		if ( (urlRoot = System.getenv("URL_ROOT")) == null ) {
			urlRoot = "http://localhost:8080/";
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
		// The purpose of this class is to retrieve all the parameters that
		// are stored in the view and put them into the exchange structure
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
		
		currentterm = request.getParameter("currentterm");
		if ( currentterm == null ) currentterm = "";

		currentpath = request.getParameter("currentpath");
		if ( currentpath == null ) currentpath = "";

		newpath = request.getParameter("newpath");
		if ( newpath == null ) newpath = "";
		
		newterm = request.getParameter("newterm");
		if ( newterm == null ) newterm = "";

		browse = request.getParameter("browse")==null?false:true;

		// select all logic is not part of GSearch
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
			currentterm, 
			newterm, 
			currentpath, 
			newpath, 
			selectall,
			unselectall, 
			allselected, 
			highlights, 
			toggle
		);
		
		StringBuilder selectedCodes = new StringBuilder();
		model.exchange.codeselect = null;
		if ( selectall || (allselected && !unselectall) ) {
			for ( int i=0, l=model.exchange.codesAvailable.size(); i<l; ++i ) {
				selectedCodes.append('1');
			}
			model.exchange.codeselect = selectedCodes.toString();
		} else if ( unselectall ) {
			model.exchange.codeselect = null;
			model.exchange.path = null;
		} else if ( browse ) {
			if (model.exchange.codeselect == null && model.exchange.path == null ) {
				// if all not selected, then copy the current state
				// but if the user clicked unselect all, then leave the array empty
				for ( int i=0, l=model.exchange.codesAvailable.size(); i<l; ++i ) {
					if ( request.getParameter(model.exchange.codesAvailable.get(i).fullFacet) != null ) {
						selectedCodes.append('1');
					} else {
						selectedCodes.append('0');
					}
				}
				model.exchange.codeselect = selectedCodes.toString();
			} else {
				model.exchange.codeselect = null;
				model.exchange.path = null;
			}
		}
		
		// process the requests with in the MODEL
		logger.fine("1: Path = " + model.exchange.path + ": Term = " + model.exchange.term + ": isAllSelected = " + model.allSelected );
		logger.fine("1: selectedCodes = " + model.exchange.codeselect);
		// store the output for the VIEW
		
		//
		Response response = client.target(urlRoot + "rest/search")
		    	.queryParam("codeselect", model.exchange.codeselect)
		    	.queryParam("path", model.exchange.path)
		    	.queryParam("term", model.exchange.term)
		    	.queryParam("highlights", model.exchange.highlights)
				.request(MediaType.APPLICATION_XML).get();
		Exchange exchange = response.readEntity(Exchange.class);
		
		if ( exchange.codesAvailable == null ) exchange.codesAvailable = new ArrayList<CodeAvailable>();
		if ( exchange.pathList == null ) exchange.pathList = new ArrayList<PathListEntry>();
		if ( exchange.selectedCodesList == null ) exchange.selectedCodesList = new ArrayList<ListEntry>();
		if ( exchange.subcodeList == null ) exchange.subcodeList = new ArrayList<ListEntry>();
		if ( exchange.path == null ) exchange.path = "";
		if ( exchange.term == null ) exchange.term = "";

        model.exchange = exchange;
		logger.fine("2: Path = " + model.exchange.path + ": Term = " + model.exchange.term + ": isAllSelected = " + model.allSelected );
		logger.fine("2: selectedCodes = " + model.exchange.codeselect);
        
        request.setAttribute("model", model );

	}
	
}

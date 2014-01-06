package com.example.servlet;

import gsearch.exchange.Exchange;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringEscapeUtils;

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
		String currentstate;
		String currentbits;
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
		
		currentstate = request.getParameter("currentstate");
		if ( currentstate == null ) currentstate = "";
		
		currentbits = request.getParameter("currentbits");

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
		if ( toggle ) highlights = !highlights;

		
		GSearchModel model = new GSearchModel(
			selectall,
			unselectall, 
			allselected 
		);

		String term = currentterm;
		if ( !currentterm.equalsIgnoreCase(newterm) ) {
			term = newterm;
			// special case to spoof browse
			if ( !newterm.isEmpty() && currentstate.equals("START")  ) {
				browse = true;
			}
		}
	
		// determine if there is a change path request ..
		boolean changepath = false;
		String path = currentpath;
		if ( !newpath.isEmpty() ) {
			path = newpath;
			changepath = true;
		}
		
		// or if we want to simulate one due to selectall 
		if ( selectall == true  ) browse = true;
		
		if ( toggle ) highlights = !highlights;

		// ok, lets keep a string of 
		String select = null;
		if ( selectall || (allselected && !unselectall) ) {
			select  = "all";
		} else if ( !unselectall ) {
			// if all not selected, then copy the current state
			// but if the user clicked unselect all, then leave the array empty
			if ( currentstate.equals("SELECT") && browse==false ) select = currentbits;
			else if ( browse==false || currentstate.equals("START") ) {
				StringBuilder selectedCodes = new StringBuilder();
				Map<String, String[]> pMap = request.getParameterMap();
				for ( String key: pMap.keySet() ) {
					if ( key.endsWith("FACET")) {
						selectedCodes.append(key.replace("FACET", "|"));
					}
				} 
				select = selectedCodes.toString();
				if ( select.isEmpty() ) select = null;
			} else if ( browse==true && !currentstate.equals("START")) {
				path = null;
			}
		}
		
		//
		Response response = client.target(urlRoot + "rest/search")
		    	.queryParam("select", select)
		    	.queryParam("path", path)
		    	.queryParam("term", term)
		    	.queryParam("highlights", highlights)
				.request(MediaType.APPLICATION_XML).get();

		Exchange exchange = response.readEntity(Exchange.class);
		response.close();
		
		if ( exchange.path == null ) exchange.path = "";
		if ( exchange.term == null ) exchange.term = "";
		exchange.term = StringEscapeUtils.escapeXml(exchange.term);

		model.exchange = exchange;
		logger.fine("Path = " + model.exchange.path + ": Term = " + model.exchange.term + ": isAllSelected = " + model.allSelected );
        
        request.setAttribute("model", model );

	}
	
}

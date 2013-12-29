package com.example.servlet;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.code.CACodes;
import com.example.model.SearchModel;

import scsb.SCSB;

/**
 * Servlet implementation class Search
 * 
 */
public class Search extends HttpServlet {
	private static final Logger logger = Logger.getLogger(Search.class.getName());

	private static final long serialVersionUID = 1L;
	
	private SCSB scsb;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
    }

    @Override
    public void init() throws ServletException {
    	super.init();
		ServletContext context = getServletContext();
		synchronized(this) {
			scsb = (SCSB)context.getAttribute("scsb");
			if ( scsb == null ) {
				try {
					
					File xmlCodes = new File(Search.class.getResource("/xmlcodes").getFile()); 
					File index = new File(Search.class.getResource("/index").getFile()); 
					File indexTaxo = new File(Search.class.getResource("/indextaxo").getFile());
					scsb = new SCSB(new CACodes(), xmlCodes, index, indexTaxo);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		        context.setAttribute("scsb", scsb);
			}
		}
    }

    @Override
    public void destroy() {
    	super.destroy();
    	try {
    		scsb.destroy();
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request);
//		response.sendRedirect(request.getContextPath() + "/Search");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/Search.jsp");
		dispatcher.forward(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request);
//		response.sendRedirect(request.getContextPath() + "/Search");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/Search.jsp");
		dispatcher.forward(request, response);
	}
	
	private void process( HttpServletRequest request ) throws IOException {
		// This is the CONTROLLER logic

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

		SearchModel model = new SearchModel(
			scsb, 
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
		
		model.handleRequest();

//model.exchange.term.
		request.setAttribute("model", model );

		logger.fine("2: Browse = " + model.exchange.browse + ": State = " + model.exchange.state );
		logger.fine("2: Path = " + model.exchange.path + ": Term = " + model.exchange.term + ": isSelectAll = " + model.allSelected );
	}

}

package com.example.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.code.ConvertCAToHybridXML;
import com.example.code.LoadCACode;

/**
 * Servlet implementation class Process
 */
public class Process extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Process() {
        super();
    }
    
    private class DoIt extends Thread {
		private File codesdir;
		private File xmlcodes;
		private File index;
		private File indextaxo;
		public boolean complete;
		public Date startTime;
    	public DoIt(File codesdir, File xmlcodes, File index, File indextaxo ) {
    		this.codesdir = codesdir;
    		this.xmlcodes = xmlcodes;
    		this.index = index;
    		this.indextaxo = indextaxo;
    		startTime = new Date();
    		complete = false;
    	}

    	@Override 
    	public void run() {
    		startTime = new Date();
    		try {
    			new ConvertCAToHybridXML().run( codesdir, xmlcodes );
    			new LoadCACode().run( codesdir, index, indextaxo);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}    		
    		complete = true;
    	}
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request);
		if ( request.getParameter("search") != null ) {
			response.sendRedirect(request.getContextPath());
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/Process.jsp");
			dispatcher.forward(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request);
		if ( request.getParameter("search") != null ) {
			response.sendRedirect(request.getContextPath());
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/Process.jsp");
			dispatcher.forward(request, response);
		}
	}
	protected void process(HttpServletRequest request) throws ServletException, IOException {
		// any point in doing a decent job of this ?
		// background it and then update a webpage .. 
		// and give status updates? .. sure . lets saw 5 minutes to run ..
		// and so we'll show 10% to start and update another 10% every .. 20 seconds?

		ServletContext context = getServletContext();
		DoIt thread = (DoIt)context.getAttribute("doit");
		String message = new String();

		if ( request.getParameter("start") != null ) {
			message = tryStart(thread, request, context);
		}
		if ( request.getParameter("stop") != null ) {
			// well, maybe someday ... 
		}
		Long percentDone = new Long(0);
		if ( thread != null ) {
			 percentDone = (new Date().getTime() - thread.startTime.getTime())/30000;
			if ( percentDone.intValue() < 10L ) percentDone = 10L;
			if ( percentDone.intValue() > 90L ) percentDone = 90L;
			if ( thread.complete == true) percentDone = 100L;
		}
		request.setAttribute("model", percentDone);
		request.setAttribute("message", message);
	}
	
	private String tryStart(DoIt thread, HttpServletRequest request, ServletContext context) {
		synchronized(this) {
			if ( thread == null ) {
				try {
					String codesString = request.getParameter("codesdir");
					if ( codesString == null ) {
						return "Please input a directory that contains the California Codes";
					} 
					File codesdir = new File(codesString);
					File xmlcodes = new File(context.getRealPath("/WEB-INF/xmlcodes"));
					File index = new File(context.getRealPath("/WEB-INF/index/"));
					File indextaxo = new File(context.getRealPath("/WEB-INF/indextaxo/"));
					thread = new DoIt( codesdir, xmlcodes, index, indextaxo );
					thread.start();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		        context.setAttribute("doit", thread);
		        return "Processing started";
			}
			if ( thread.complete == true ) {
		        context.removeAttribute("doit");
			}
		}
		return "";
	}

}

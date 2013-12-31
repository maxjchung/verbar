package com.example.model;

import java.io.IOException;

import exchange.Exchange;
import scsb.SCSB;

public class SearchModel {

	public Exchange exchange;
	private SCSB scsb;
	// working var ...
	public boolean allSelected;
		
	public SearchModel(
		SCSB scsb, 
		String currentstate, 
		String currentterm, 
		String newterm, 
		String currentpath, 
		String newpath, 
		boolean browse, 
		boolean selectall, 
		boolean unselectall, 
		boolean allselected, 
		boolean highlights, 
		boolean toggle
		) throws IOException {
		this.scsb = scsb;
		int state = Integer.parseInt(currentstate);
		// determine if the term has changed ..
		String term = currentterm;
		if ( !currentterm.equalsIgnoreCase(newterm) ) {
			term = newterm;
			// special case to spoof browse
			if ( !newterm.isEmpty() && state == SCSB.STATE_ONE ) {
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
		
		// set an stateflag for the view (Search.jsp)
		if ( selectall == true || (allselected == true && unselectall != true ) ) {
			allSelected = true;
		}

		if ( toggle ) highlights = !highlights;
		
		exchange = scsb.getExchange(
			state, 
			term, 
			path, 
			changepath, 
			browse, 
			highlights
		);
	}
	
	public void handleRequest() throws IOException {
		scsb.handleRequest( exchange );		
	}
	
	/*
	 * Definitely need to fix this up ... 
	 */
	public String highlightText( String text, String term, String preTag, String postTag ) {
		if ( term == null || term.isEmpty() ) return text;
		String lText = text.toLowerCase();
		String lTerm = term.toLowerCase();
		StringBuffer buffer = new StringBuffer();
		int cpos = 0;
		int idx = lText.indexOf(lTerm, cpos);
		while ( idx != -1 ) {
			buffer.append(text.substring(cpos, idx));
			buffer.append(preTag);
			buffer.append(text.substring(idx, idx + term.length()));
			buffer.append(postTag);
			cpos = idx + lTerm.length();
			idx = lText.indexOf(lTerm, cpos);
		}
		buffer.append(text.substring(cpos));
		return buffer.toString();
	}
}
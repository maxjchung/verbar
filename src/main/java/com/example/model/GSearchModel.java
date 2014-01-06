package com.example.model;

import java.io.IOException;

import gsearch.exchange.*;

public class GSearchModel {

	public Exchange exchange;

	public boolean allSelected;

	public GSearchModel(
		boolean selectall, 
		boolean unselectall, 
		boolean allselected
	) throws IOException {
		// set an stateflag for the view (Search.jsp)
		if ( selectall == true || (allselected == true && unselectall != true ) ) {
			allSelected = true;
		}

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

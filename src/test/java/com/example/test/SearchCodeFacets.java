package com.example.test;

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.facet.search.FacetResultNode;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.facet.search.CountFacetRequest;
import org.apache.lucene.facet.params.FacetSearchParams;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import codesparser.CodeReference;


public class SearchCodeFacets {
		Logger logger = Logger.getLogger(SearchCodeFacets.class.getName());

		@BeforeClass
		public static void testSetup() {
		}

		@AfterClass
		public static void testCleanup() {
		  // Teardown for data used by the unit tests
		}

		private Query makeQuery() {
			Query q;
	    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
	    	QueryParser parser = new QueryParser(Version.LUCENE_44, "sectiontext", analyzer);
	    	try {
	    		q = parser.parse("test");
	    	} catch ( ParseException pe ) {
	    		throw new RuntimeException( pe );
	    	}
			return q;
		}

		@Test
	    public void testApp() throws Exception
	    {
//	    	return;
	    // open readers
        IndexReader indexReader = DirectoryReader.open( FSDirectory.open(new File("c:/users/karl/workspace/lawstick/src/main/webapp/WEB-INF/index")));
        IndexSearcher searcher = new IndexSearcher(indexReader);
	    TaxonomyReader taxoReader = new DirectoryTaxonomyReader(FSDirectory.open(new File("c:/users/karl/workspace/lawstick/src/main/webapp/WEB-INF/indextaxo")));
	    	
		Query query = makeQuery();
		
		FacetSearchParams facetSearchParams = null;		    
		FacetsCollector facetsCollector = null;
		Collector collector = null;
//		TopScoreDocCollector topScoreDocCollector = null;

		
//		List<FacetRequest> listFacetRequest = new ArrayList<FacetRequest>();
	    CategoryPath categoryPath = new CategoryPath("business-0");
//	    listFacetRequest.add(new CountFacetRequest( categoryPath, 20));
//	    facetSearchParams = new FacetSearchParams(listFacetRequest);
	    facetSearchParams = new FacetSearchParams(new CountFacetRequest( categoryPath, 30));
/*    		    
		CategoryPath catPath = new CategoryPath("business-0", CodeReference.PATHSEPARATOR); 
	    facetSearchParams = new FacetSearchParams(new CountFacetRequest( catPath, 200));
	    FacetIndexingParams facetIndexingParams = new FacetIndexingParams(new CategoryListParams());
    	// the the search parameters into a results collector
	    DrillDownQuery drillDownQuery = new DrillDownQuery(facetIndexingParams, query);// .query(query, catPath);
	    drillDownQuery.add(catPath);
	    query = drillDownQuery;
*/

/**/
//	    facetSearchParams = new FacetSearchParams(new CountFacetRequest( categoryPath, 30));
//		facetsCollector = FacetsCollector.create(facetSearchParams, indexReader, taxoReader);
//		collector = MultiCollector.wrap( new TotalHitCountCollector(), facetsCollector );

//		facetsCollector = FacetsCollector.create(facetSearchParams, indexReader, taxoReader);
//		collector = facetsCollector;

//		facetsCollector = FacetsCollector.create(facetSearchParams, indexReader, taxoReader);
//		collector = facetsCollector;

/**/
    	// the the search parameters into a results collector
		facetsCollector = FacetsCollector.create(facetSearchParams, indexReader, taxoReader);

    	Collector totalsCollector = new TotalHitCountCollector();
	    searcher.search(query, MultiCollector.wrap(totalsCollector, facetsCollector));

//    	List<FacetResult> facetResults = facetsCollector.getFacetResults();
//    	searcher.search(query, facetsCollector);
/**/
    	List<FacetResult> facetResults = facetsCollector.getFacetResults();
    	Iterator <FacetResult> frit = facetResults.iterator();
    	while ( frit.hasNext() ) {
    		FacetResult fResult = frit.next();
    		FacetResultNode facetNode = fResult.getFacetResultNode();
    		
	    	CategoryPath cPath = facetNode.label;
	    	String strPath = cPath.toString( CodeReference.PATHSEPARATOR );
	    	logger.info( strPath + ":" + facetNode.value);
	    	double total = 0.0;

		    Iterator<? extends FacetResultNode> subit = facetNode.subResults.iterator();
		    while ( subit.hasNext() ) {
		    	FacetResultNode facetSubNode = subit.next();
		    	total += facetSubNode.value;
		    	
		    	CategoryPath cSubPath = facetSubNode.label;
		    	String strSubPath = cSubPath.toString(CodeReference.PATHSEPARATOR);

		    	logger.info( strSubPath + ":" + facetSubNode.value);

		    }
	    	logger.info( strPath + ":Total:" + total);
    	}
/*
    	List<FacetResult> facetResults = facetsCollector.getFacetResults();
    	// specific path, so get the result
	    FacetResultNode facetResultNode = facetResults.get(0).getFacetResultNode();
	    Iterator<? extends FacetResultNode> subit = facetResultNode.subResults.iterator();
	    while ( subit.hasNext() ) {
	    	FacetResultNode facetNode = subit.next();
	    	
	    	CategoryPath cPath = facetNode.label;
	    	String strPath = cPath.toString(CodeReference.PATHSEPARATOR);

	    	logger.info( strPath + ":" + facetNode.value);

	    }
*/	    
    	
	    // close readers
	    taxoReader.close();
	    indexReader.close();
    }

}
/*	    
CategoryPath categoryPath2 = new CategoryPath("GOVERNMENT-1");
CountFacetRequest cfRequest2 = new CountFacetRequest( categoryPath2, 20);
facetSearchParams.addFacetRequest(cfRequest2);

CategoryPath categoryPath3 = new CategoryPath("INSURANCE-1");
CountFacetRequest cfRequest3 = new CountFacetRequest( categoryPath3, 20);
facetSearchParams.addFacetRequest(cfRequest3);
FacetsCollector facetsCollector = new FacetsCollector(facetSearchParams, indexReader, taxoReader);

searcher.search(q, facetsCollector);

System.out.println( facetsCollector.getFacetResults().size() );

FacetResult facetResult = facetsCollector.getFacetResults().get(0);
System.out.println( facetResult.getFacetResultNode().getValue());

FacetResultNode facetResultNode = facetResult.getFacetResultNode();
//System.out.println(facetResults);

FacetResultNode subNode = facetResultNode.getSubResults().iterator().next(); 
System.out.println(subNode);

CategoryPath cPath = subNode.getLabel();
//System.out.println("cPath = " + cPath + " count = " + facetNode.getValue());
String strPath = cPath.toString('|');
System.out.println(strPath);

//System.out.println(facetResults);

/*
FacetResult facetResult2 = facetsCollector.getFacetResults().get(1);
System.out.println( facetResult2.getFacetResultNode().getValue());

FacetResult facetResult3 = facetsCollector.getFacetResults().get(2);
System.out.println( facetResult3.getFacetResultNode().getValue());
*/	    
/*	    
FacetResultNode facetResultNode = facetResults.get(0).getFacetResultNode();

FacetResultNode subNode = facetResultNode.getSubResults().iterator().next(); 
//System.out.println(facetResults);

//    System.out.println(facetResults);
// Get the first facet results ..

// Next
FacetResult facetResult2 = facetResults.get(0);

FacetResultNode facetResultNode2 = facetResult2.getFacetResultNode().getSubResults().iterator().next();

FacetSearchParams facetSearchParams2 = new FacetSearchParams();
facetSearchParams2.addFacetRequest(new CountFacetRequest( facetResultNode2.getLabel(), 20));

FacetsCollector facetsCollector2 = new FacetsCollector(facetSearchParams2, indexReader, taxoReader);
searcher.search(q, facetsCollector2 );
List<FacetResult> facetResults2 = facetsCollector2.getFacetResults();
//System.out.println(facetResults2);
System.out.println(facetResults2.size());

// Next
FacetResult facetResult3 = facetResults2.get(0);

FacetResultNode facetResultNode3 = facetResult3.getFacetResultNode().getSubResults().iterator().next();

FacetSearchParams facetSearchParams3 = new FacetSearchParams();
facetSearchParams3.addFacetRequest(new CountFacetRequest( facetResultNode3.getLabel(), 20));

FacetsCollector facetsCollector3 = new FacetsCollector(facetSearchParams3, indexReader, taxoReader);
searcher.search(q, facetsCollector3);
List<FacetResult> facetResults3 = facetsCollector3.getFacetResults();

//System.out.println(facetResults3);
System.out.println(facetResults3.size());

// Next
FacetResult facetResult4 = facetResults3.get(0);

FacetResultNode facetResultNode4 = facetResult4.getFacetResultNode().getSubResults().iterator().next();

FacetSearchParams facetSearchParams4 = new FacetSearchParams();
facetSearchParams4.addFacetRequest(new CountFacetRequest( facetResultNode4.getLabel(), 20));
FacetsCollector facetsCollector4 = new FacetsCollector(facetSearchParams4, indexReader, taxoReader);

CategoryPath cPath = facetResultNode4.getLabel();
char[] outputBuffer = new char[cPath.charsNeededForFullPath()];
cPath.copyToCharArray(outputBuffer, 0, cPath.length(), '|');
System.out.println(outputBuffer);
//Query q2 = new TermQuery(new Term("path", new String(outputBuffer) ));
//Query q2 = new TermQuery(new Term("sectiontext", "marriage" ));
*/

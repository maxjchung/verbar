package com.example.code;


import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.index.FacetFields;
import org.apache.lucene.facet.params.CategoryListParams;
import org.apache.lucene.facet.params.FacetIndexingParams;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import scsb.load.LuceneCodeModel;
import scsb.load.LuceneSectionModel;
import codesparser.Code;
import codesparser.CodeReference;
import codesparser.Section;
import codesparser.SectionNumber;
import codesparser.CodeRange;

public class LoadCACode {

	private static Logger logger = Logger.getLogger(LoadCACode.class.getName());
	private static final String DEBUGFILE = "bpc"; // null; 	// "gov"; // "fam"; 

	private IndexWriter indexWriter;
	private TaxonomyWriter taxoWriter;
	private FacetFields facetFields;

	int nDocsAdded;
    int nFacetsAdded;
	
	public static void main(String[] args) throws Exception {
//        logger.setLevel(Level.INFO);

        new LoadCACode().run(
        		new File( "c:/users/karl/code" ),         		
        		new File("c:/users/karl/scsb/heroku/verbar/src/main/resources/index"), 
        		new File("c:/users/karl/scsb/heroku/verbar/src/main/resources/indextaxo") );
	}
	
	public LoadCACode() {
	}
	
	public void run(File codesdir, File index, File indextaxo) throws Exception {
		Date start = new Date();
		CACodes caCodes = new CACodes();

		logger.info("Indexing to directory 'index'...");

		Directory indexDir = FSDirectory.open(index);
		Directory taxoDir = FSDirectory.open(indextaxo);

		
		// Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_45);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
//		Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_45);
//		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_45);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45, analyzer);
		// Create a new index in the directory, removing any
		// previously indexed documents:
		iwc.setOpenMode(OpenMode.CREATE);
	    // create and open an index writer
	    indexWriter = new IndexWriter(indexDir, iwc);
	    // create and open a taxonomy writer
	    taxoWriter = new DirectoryTaxonomyWriter(taxoDir, OpenMode.CREATE);
	    
		FacetIndexingParams fip = new FacetIndexingParams(new CategoryListParams() {
			@Override
			public OrdinalPolicy getOrdinalPolicy(String dim) {
				// NO_PARENTS also works:
				return OrdinalPolicy.ALL_PARENTS;
			}
		});

		facetFields = new FacetFields(taxoWriter, fip);

	    nDocsAdded = 0;
	    nFacetsAdded = 0;

		// dreams and aspirations
	    File[] files = codesdir.listFiles( new FileFilter() {

			public boolean accept(File pathname) {
				if ( pathname.isDirectory() ) return false;
				if (pathname.getName().toString().contains("constitution"))
					return false;
				if ( DEBUGFILE != null ) { 
					if (!pathname.getName().toString().contains(DEBUGFILE)) return false;
				}
				return true;
			}} );
	    
	    for ( int i=0;i<files.length; ++i ) {
			logger.info("Processing " + files[i]);
			processFile(caCodes, codesdir, files[i]);
		}
		taxoWriter.commit();
		indexWriter.commit();

		taxoWriter.close();
		indexWriter.close();

		Date end = new Date();
		logger.info(end.getTime() - start.getTime() + " total milliseconds");
		logger.info("From " + "codes" + " " + nDocsAdded + ": Facets = " + nFacetsAdded);
	}

	private void processFile(CACodes caCodes, File codesdir, File file) throws Exception {
		CodeParser parser = new CodeParser();
		Code c = parser.parse(caCodes, "ISO-8859-1", file);
		String abvr = file.getName().toString().substring(0, file.getName().toString().indexOf('_'));
/*
		// debug code
		CodeReference reference = c.findReference(new SectionNumber("2150"));
		processReference( reference, codesdir.getPath() + "/" + abvr );
		// debug code
*/		
		iterateReferences( caCodes, c.getReferences(), codesdir.getPath() + "/" + abvr);		
	}
	
	private void iterateReferences( CACodes codes, ArrayList<CodeReference> references, String basepath ) throws Exception {
		// Iterator over sections ..
		for ( CodeReference reference: references ) { 
        	// keep going until we get into a section
        	if ( reference.getReferences() != null ) iterateReferences(codes, reference.getReferences(), basepath);
        	processReference( codes, reference, basepath );
        }
	}
	
	/*
	 * I need to save the title, the categorypath, the full path for reference, the text, the part and partnumber
	 * and of course the section and sectionParagraph if it exists
	 */
	private void processReference( CACodes codes, CodeReference reference, String basepath ) throws Exception {
		LuceneCodeModel model = new LuceneCodeModel( reference );
		
//		SectionRange range = reference.getSectionRange();
//		if (range != null) {
		Section section = reference.returnSection();
		if (section != null) {
			CodeRange range = reference.getCodeRange();
			String strRange = range.getsNumber();
			String firstInt = new String();
			for (int i = 0, il = strRange.length(); i < il; ++i) {
				char ch = strRange.charAt(i);
				if (Character.isDigit(ch)) {
					firstInt = firstInt.concat("" + ch);
				} else {
					break;
				}
			}
			int num = Integer.parseInt(firstInt);
			num = ((num - 1) / 1000) * 1000;
			String subdir = String.format("%05d-%05d", num + 1, num + 1000);
			if ( range.geteNumber() != null ) strRange = strRange + "-" + range.geteNumber();
			String strPath = new String(basepath + "\\" + subdir + "\\" + strRange);
			File codeDetail = new File(strPath);
			ArrayList<String>sections = SectionParser.parseSectionFile("ISO-8859-1", codeDetail, reference); //  parseParagraph( codeDetail, model );
			parseSectionModels(sections, model );
		}
		
		if ( model.getSections().size() == 0 ) {
			writeDocument(codes, reference, "", "");
			
		} else {
			for ( LuceneSectionModel sectionModel: model.getSections() ) {
				writeDocument(codes, reference, sectionModel.getSectionNumber().toString(), sectionModel.getSectionParagraph());
			}
		}		
	}
	
	private void writeDocument(CACodes codes, CodeReference reference, String sectionNumber, String sectionText) throws Exception {
		logger.fine( "writeDocument ... ");
		Document doc = new Document();

		String part = reference.getPart();
		if ( part == null ) part = "";

		String partNumber = reference.getPartNumber();
		if ( partNumber == null ) partNumber = "";

		CodeRange range = reference.getCodeRange();
		String codeRange; 
		if ( range == null ) codeRange = "";
		else codeRange = range.getsNumber() + " - " + range.geteNumber();

		doc.add(new StringField("path", reference.returnFullpath(), Field.Store.YES));

		doc.add(new StringField("part", part , Field.Store.YES));

		doc.add(new StringField("partnumber", partNumber, Field.Store.YES));

		doc.add(new TextField("title", reference.getTitle(), Field.Store.YES));

		doc.add(new StringField("sectionnumber", sectionNumber, Field.Store.YES));

		doc.add(new StringField("coderange", codeRange, Field.Store.YES));

//		doc.add(new VecTextField("sectiontext", sectionText, Field.Store.YES));
		doc.add(new TextField("sectiontext", sectionText, Field.Store.YES));

		// obtain the sample facets for current document
		List<CategoryPath> facetList = generateFacetPath(codes, reference);
		logger.fine( facetList.toString());
		// we do not alter indexing parameters!  
		// a category document builder will add the categories to a document once build() is called
//		CategoryDocumentBuilder categoryDocBuilder = new CategoryDocumentBuilder(taxoWriter);
//		categoryDocBuilder.setCategoryPaths(facetList);
		// invoke the category document builder for adding categories to the document and,
		// as required, to the taxonomy index 
//		categoryDocBuilder.build(doc);
		
		facetFields.addFields(doc, facetList);
		
		// finally add the document to the index
		indexWriter.addDocument(doc);
		
		nDocsAdded++;
		nFacetsAdded += facetList.size(); 
		
	}

	private void parseSectionModels(ArrayList<String> sections, LuceneCodeModel model ) throws Exception {
		logger.fine("Number of sections = " + sections.size());
		for ( String sect: sections ) {
			logger.fine(sect.substring(0, sect.length()>20?20:sect.length() ) + " ..." );
			SectionNumber PNumber = SectionParser.getSectionNumber(sect);
			
			LuceneSectionModel sectionModel = new LuceneSectionModel( PNumber, sect );
			model.getSections().add(sectionModel);

		}
	}

	private ArrayList<CategoryPath> generateFacetPath(CACodes codes, CodeReference reference ) {
		ArrayList<String> strings = new ArrayList<String>();
		ArrayList<CategoryPath> categoryPaths = new ArrayList<CategoryPath>();
		CodeReference codeReference = reference;
		while ( codeReference != null ) {
			strings.add( codeReference.returnPartPath() );
			codeReference = codeReference.getParent();
		}
		Collections.reverse(strings);
		CategoryPath categoryPath = new CategoryPath(strings.toArray(new String[strings.size()]));
		categoryPaths.add(categoryPath);
		return categoryPaths;
	}

}

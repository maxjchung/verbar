package com.example.code;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import scsb.CodesInterface;
import codesparser.Code;
import codesparser.CodeReference;
import codesparser.SectionNumber;

/**
 * Created with IntelliJ IDEA. User: karl Date: 6/7/12 Time: 5:37 AM To change
 * this template use File | Settings | File Templates.
 */
public class CACodes implements CodesInterface {
	private final static Logger logger = Logger.getLogger( CACodes.class.getName() );
    private static final String DEBUGFILE = null; // "bpc";	// "fam";

	private ArrayList<Code> codes;
	private CodeParser parser;
	private static class CodeTitles {
		public String facetHead;
		public String shortTitle;
		public CodeTitles(String facetHead, String shortTitle) {
			this.facetHead = facetHead;
			this.shortTitle = shortTitle;
		}
	}
	private static HashMap<String, CodeTitles> mapCodeToTitles;
	static {
		mapCodeToTitles = new HashMap<String, CodeTitles> (); 
		mapCodeToTitles.put( "CALIFORNIA BUSINESS AND PROFESSIONS CODE".toLowerCase(), new CodeTitles( "business", "Bus. & Professions") );
/*		
		mapCodeToTitles.put( "CALIFORNIA CODE OF CIVIL PROCEDURE".toLowerCase(), new CodeTitles( "procedure", "Civ. Procedure") );
		mapCodeToTitles.put( "CALIFORNIA CIVIL CODE".toLowerCase(), new CodeTitles("civil", "Civil") );
		mapCodeToTitles.put( "CALIFORNIA COMMERCIAL CODE".toLowerCase(), new CodeTitles( "commercial", "Commercial") );
		mapCodeToTitles.put( "CALIFORNIA CORPORATIONS CODE".toLowerCase(), new CodeTitles( "corporations", "Corporations" ) );
		mapCodeToTitles.put( "CALIFORNIA EDUCATION CODE".toLowerCase(), new CodeTitles( "education", "Education") );
		mapCodeToTitles.put( "CALIFORNIA ELECTIONS CODE".toLowerCase(), new CodeTitles( "elections", "Elections") );
		mapCodeToTitles.put( "CALIFORNIA EVIDENCE CODE".toLowerCase(), new CodeTitles( "evidence", "Evidence" ) );
		mapCodeToTitles.put( "CALIFORNIA FOOD AND AGRICULTURAL CODE".toLowerCase(), new CodeTitles( "agriculture", "Agriculture") );
		mapCodeToTitles.put( "CALIFORNIA FAMILY CODE".toLowerCase(), new CodeTitles( "family", "Family") );
		mapCodeToTitles.put( "CALIFORNIA FISH AND GAME CODE".toLowerCase(), new CodeTitles( "game", "Fish & Game" ) );
		mapCodeToTitles.put( "CALIFORNIA FINANCIAL CODE".toLowerCase(), new CodeTitles( "finacial", "Financial" ) );
		mapCodeToTitles.put( "CALIFORNIA GOVERNMENT CODE".toLowerCase(), new CodeTitles( "government", "Government") );
		mapCodeToTitles.put( "CALIFORNIA HARBORS AND NAVIGATION CODE".toLowerCase(), new CodeTitles( "harbors", "Harbors & Nav." ) );
		mapCodeToTitles.put( "CALIFORNIA HEALTH AND SAFETY CODE".toLowerCase(), new CodeTitles( "health", "Health") );
		mapCodeToTitles.put( "CALIFORNIA INSURANCE CODE".toLowerCase(), new CodeTitles( "insurance", "Insurance") );
		mapCodeToTitles.put( "CALIFORNIA LABOR CODE".toLowerCase(), new CodeTitles( "labor", "Labor") );
		mapCodeToTitles.put( "CALIFORNIA MILITARY AND VETERANS CODE".toLowerCase(), new CodeTitles( "miltary", "Military & Vets." ) );
		mapCodeToTitles.put( "CALIFORNIA PUBLIC CONTRACT CODE".toLowerCase(), new CodeTitles( "contract", "Public Contact") );
		mapCodeToTitles.put( "CALIFORNIA PENAL CODE".toLowerCase(), new CodeTitles( "penal", "Penal") );
		mapCodeToTitles.put( "CALIFORNIA PUBLIC RESOURCES CODE".toLowerCase(), new CodeTitles( "resources", "Public Resources") );
		mapCodeToTitles.put( "CALIFORNIA PROBATE CODE".toLowerCase(), new CodeTitles( "probate", "Probate") );
		mapCodeToTitles.put( "CALIFORNIA PUBLIC UTILITIES CODE".toLowerCase(), new CodeTitles( "utilities", "Public Utilities" ) );
		mapCodeToTitles.put( "CALIFORNIA REVENUE AND TAXATION CODE".toLowerCase(), new CodeTitles( "revenue", "Revenue & Tax." ) );
		mapCodeToTitles.put( "CALIFORNIA STREETS AND HIGHWAYS CODE".toLowerCase(), new CodeTitles( "highways", "Highways" ) );
		mapCodeToTitles.put( "CALIFORNIA UNEMPLOYMENT INSURANCE CODE".toLowerCase(), new CodeTitles( "unemployment", "Unemployment Ins." ) );
		mapCodeToTitles.put( "CALIFORNIA VEHICLE CODE".toLowerCase(), new CodeTitles( "vehicle", "Vehicle" ) );
		mapCodeToTitles.put( "CALIFORNIA WATER CODE".toLowerCase(), new CodeTitles( "water", "Water" ) );
		mapCodeToTitles.put( "CALIFORNIA WELFARE AND INSTITUTIONS CODE".toLowerCase(), new CodeTitles( "welfare", "Welfare & Inst." ) );
*/		
	}

    public static final String[] sectionTitles = {
        "title",
        "part",
        "division",
        "chapter",
        "article"
    };

	public static final String[] patterns = {
            "business and professions code",
  /*          "code of civil procedure",
            "civil code",
            "commercial code",
            "corporations code",
            "education code",
            "elections code",
            "evidence code",
            "food and agricultural code",
            "family code",
            "fish and game code",
            "financial code",
            "government code",
            "harbors and navigation code",
            "health and safety code",
            "insurance code",
            "labor code",
            "military and veterans code",
            "public contract code",
            "penal code",
            "public resources code",
            "probate code",
            "public utilities code",
            "revenue and taxation code",
            "streets and highways code",
            "unemployment insurance code",
            "vehicle code",
            "water code",
            "welfare and institutions code",
            "code of civil procedure"
*/            
    };

/*
    public static String getShortTitle(String title) {
        if ( title == null ) return title;
        for (int i=0; i < patterns.length; ++i ) {
            if ( title.toLowerCase().contains(patterns[i]) )
                return patternsAbvr[i];
        }
        return title;
    }
*/    

    public static final String[] patternsAbvr = {
            "bus. & prof. code",
/*            "code civ. proc.",
            "civ. code",
            "com. code",
            "corp. code",
            "ed. code",
            "elec. code",
            "evid. code",
            "food & agr. code",
            "fam. code",
            "fish & game code",
            "fin. code",
            "gov. code",
            "har. & nav. code",
            "health & saf. code",
            "ins. code",
            "lab. code",
            "mil. and vet. code",
            "pub. con. code",
            "pen. code",
            "pub. res. code",
            "prob. code",
            "pub. util. code",
            "rev. & tax. code",
            "st. & high. code",
            "unemp. ins. code",
            "veh. code",
            "wat. code",
            "welf. & inst. code",
            "code of civ. pro."
*/            
    };

	public CACodes() {
		parser = new CodeParser();
		codes = new ArrayList<Code>();
	}

	/*
	 * There is a problem here. When using this method, the section numbers in  
	 * are not in consistent order. e.g.  Penal Code 273a is before 273.1
	 * but 270a is after 270.1 -- This makes is difficult, or impossible, to determine
	 * what file a specific section number belongs to. I'm coding it so that
	 * 270a is said to come before 270.1. This is needed because the files
	 * 270-273.5 includes 273a. The file 273.8-273.88 does not include 273a.
	 * I don't know if there are other situations where this is reversed ... 
	 * I should write a utility to check everything. See ConvertToHybridXML in the
	 * SCSB project.
	 * ...
	 * ok, there's more. The second numerical element of the section number is not ordered numberically, but lexically.
	 * so .. 422.865 comes before 422.88
	 */
	public void loadFromRawPath(File path) throws FileNotFoundException {
		// ArrayList<File> files = new ArrayList<File>();

		File[] files = path.listFiles( 
			new FileFilter() {
			public boolean accept(File file) {
				if (file.getName().toString().contains("constitution"))
					return false;
				if ( DEBUGFILE != null ) { 
					if (!file.getName().toString().contains(DEBUGFILE)) return false;
				}
				if ( file.isDirectory() ) return false;
				return true;
			}
			
		} );
		
		for ( int i=0; i < files.length; ++i ) {
			logger.info("Processing " + files[i]);
			loadRawFile( "ISO-8859-1", files[i] );
		}

	}
	

	public void loadFromXMLPath(File path) throws Exception {
		File[] files = path.listFiles( 
				new FileFilter() {
				public boolean accept(File file) {
					// if ( directory.isDirectory() ) return false;
					if (file.getName().toString().contains("constitution"))
						return false;
					if ( DEBUGFILE != null ) { 
						if (!file.getName().toString().contains(DEBUGFILE)) return false;
					}
					return true;
				}
				
			} );
			
		for (int i = 0; i < files.length; ++i) {
			logger.info("Processing " + files[i]);
			loadXMLFile(files[i]);
		}
		Collections.sort( codes );
	}

	public void loadXMLFile(File file) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return null; // Never resolve any IDs
            }
        });
        
        Document xmlDoc = db.parse( new FileInputStream( file ) );

        NodeList nodeList = xmlDoc.getChildNodes();
        
        for ( int di=0, dl = nodeList.getLength(); di<dl; ++di ) {
    		Node node = nodeList.item(di);
    		String nname = node.getNodeName();
	        if  ( nname.equals(CodeReference.CODE) ) {
	        	codes.add(new Code(node));
	        	return;
			}
        }
        return;
	}
	
	public void loadRawFile(String encoding, File file) throws FileNotFoundException {
		codes.add( parser.parse(this, encoding, file) );
	}

	public CodeReference findReference(String codeTitle, SectionNumber sectionNumber) {
		return findReference(codeTitle).findReference( sectionNumber );
	}

	public Code findReference(String codeTitle) {
		String tempTitle = codeTitle.toLowerCase();
		Iterator<Code> ci = codes.iterator();
		while (ci.hasNext()) {
			Code code = ci.next();
			if (code.getTitle().toLowerCase().contains(tempTitle)) {
				return code;
			}
		}
		throw new RuntimeException("Code not found:" + codeTitle);
	}

	private CodeReference findReferenceByShortTitle(String shortTitle) {
		Iterator<Code> ci = codes.iterator();
		while (ci.hasNext()) {
			Code code = ci.next();
			if (code.getShortTitle().equals(shortTitle)) {
				return code;
			}
		}
		throw new RuntimeException("Code not found:" + shortTitle);
	}

	public CodeReference findReferenceByFacet(String shortTitle, String fullFacet) {
		return findReferenceByShortTitle(shortTitle).findReferenceByFacet( fullFacet );
	}

// need something to build and manage "paths" .. 
// ie strings that indicate the "part" and "partNumber" of each reference
// leading up to the parent of the code section.. which needs its
	// own path entry as well.
	// for ex: family-1|division-3|chapter-3|article-5.6
	// we need to ..
	// generate a fullpath for any reference
	// find the code (any level) for any fullpath generated ..
	// ensure that the fullpath for just a code parent is unqiue
	// it's essentially a string-id into the code base.
	// also, the path needs to be parsable so as to point to a node
	// in the code .. or better a routine here to take a fullpath
	// and return the correct reference within with code heirarchy.
	// find the code and then it can be used to generate a return path
	// of references .. 
	// which it may well do already..
	// the only real issue is the top level .. and some of its first branches
	// which doesn't have any part-partNumber, so one has to be made up
	// question, do it on the fly, or, really, perhaps, when the code is 
	// first parsed from leginfo's raw codes ...
	// there MUST be a better way .. :)
	// 
/*	
	public Code findCode(String codeTitle) {
		String tempTitle = codeTitle.toLowerCase();
		Iterator<Code> ci = codes.iterator();
		while (ci.hasNext()) {
			Code code = ci.next();
			if (code.getTitle().toLowerCase().contains(tempTitle)) {
				return code;
			}
		}
		return null;
	}
	public static String generateFullPath(CodeReference reference ) {
		String header = new String();
		CodeReference codeReference = reference;
		while ( codeReference != null ) {
			String part = codeReference.getPart();
			String partNumber = codeReference.getPartNumber();
			if ( part == null ) {
				String tpart = codeReference.getTitle().trim().replace("CALIFORNIA", "").trim();
		    	part = tpart.substring(0, tpart.indexOf(" ", 0));	// CALIFORNIA
				partNumber = "1";
			}
			header = part + "-" + partNumber + header;
//			header = part + "-" + partNumber + header;
			codeReference = codeReference.getParent();
			if ( codeReference != null ) header = "|" + header;
		}
		return header;
	}

*/	
	public Code findCodeFromPath( String fullPath ) {
		String mapValue = fullPath.substring(0, fullPath.indexOf('-')).toLowerCase();
		Iterator<Entry<String, CodeTitles>> sit = mapCodeToTitles.entrySet().iterator();
		while ( sit.hasNext() ) {
			Entry<String, CodeTitles> entry = sit.next();
			if ( entry.getValue().facetHead.equals( mapValue ) ) {
				return findReference(entry.getKey());
			}
		}
		throw new RuntimeException("Code Not Found:" + fullPath);
	}

	public String mapCodeToFacetHead(String title) {
		return mapCodeToTitles.get(title.toLowerCase()).facetHead; 
	}
	
	public String getShortTitle(String title) {
		return mapCodeToTitles.get(title.toLowerCase()).shortTitle; 
	}

	public ArrayList<Code> getCodes() {
		return codes;
	}
	
	public static void main(String[] args) throws Exception {
//		logger.setLevel(Level.FINE);
		CACodes codes = new CACodes();
		codes.loadFromRawPath(new File("c:/users/karl/code"));
//		codes.loadFromXMLPath( new File("src/main/webapp/WEB-INF/xmlcodes"));
		// CodeParser parser = new CodeParser();
//		Path path = FileSystems.getDefault().getPath("codes/ccp_table_of_contents");
//		Path path = ;		// <--|
//		Code c = parser.parse(path);		// <--|
		CodeReference reference = codes.findReference("California Penal Code", new SectionNumber("625") );
		System.out.println(reference );
		System.out.println( reference.returnFullpath());
	}

}

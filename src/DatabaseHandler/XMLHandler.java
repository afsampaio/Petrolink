package DatabaseHandler;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import PetroLink.Coordinates;
import PetroLink.Simulator;

public class XMLHandler extends Writer implements Runnable{
	private String fileName;
	private Document doc;
	private Element rootElement, dataValuesElement, dataElement;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	Element current;
	
	public XMLHandler(String file, Simulator sim) {
		super(file, sim);
	}
	
	public void setupWriterDom() throws ParserConfigurationException {
		this.dbf = DocumentBuilderFactory.newInstance();
		this.db = dbf.newDocumentBuilder();
		this.doc = db.newDocument();
		
		Element rootElement = doc.createElement("results");
		doc.appendChild(rootElement);
		
		Element dataValues = doc.createElement("data_values");
		rootElement.appendChild(dataValues);
		this.current = dataValues;
	}
	
	public void appendToFileDom(Coordinates coord) throws XMLStreamException {
		Element data = this.doc.createElement("data");
		this.current.appendChild(data);
		
		Element x = doc.createElement("x");
		x.appendChild(this.doc.createTextNode(Double.toString(coord.getx())));
		data.appendChild(x);
		
		Element y = doc.createElement("y");
		y.appendChild(this.doc.createTextNode(Double.toString(coord.gety())));
		data.appendChild(y);
		
		Element z = doc.createElement("z");
		z.appendChild(this.doc.createTextNode(Double.toString(coord.getz())));
		data.appendChild(z);
	}
	
	public void closeWriterDom() throws TransformerException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,"yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        this.doc.setXmlStandalone(true);
		
        DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(this.getDestinationName()));
		transformer.transform(source, result);
	}
	
	public  void writeToDestination(Coordinates coords) {
		try {
			this.appendToFileDom(coords);
		} catch (XMLStreamException e) {
			System.out.println("XMLHandler: Couldn't insert coordinates into destination file: " + e.getMessage());
		}
	}
	
	public void closeDestination() {
		try {
			this.closeWriterDom();
		} catch (TransformerException e) {
			System.out.println("XMLHandler: Problem closing destination file: " + e.getMessage());
		}
	}
	
	public void run() {
		try {
			this.setupWriterDom();
		} catch (ParserConfigurationException e) {
			System.out.println("XMLHandler: Couldn't setup Handler: " + e.getMessage());
		}
		
		boolean active = true;
		int index = 0;
		Coordinates coords;
		
		while(active) {
			coords = this.getFromRegistry(index);
			//System.out.println("XMLHandler: Got coordinates from registry");
			
			if(coords != null) {
				this.writeToDestination(coords);
				index++;
				active = this.getSimulator().getStatusAndEnd(index);
			}
			else {
				active = this.getSimulator().getStatus();
			}
		}
		
		this.closeDestination();
	}
	
	/*Deprecated Source
	public void setupWriter() throws XMLStreamException, FileNotFoundException, UnsupportedEncodingException, FactoryConfigurationError { //Deprecated
		this.os = new FileOutputStream(new File(fileName));
		
		this.out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(this.os, "UTF8"));
		
		this.out.writeStartDocument("utf-8", "1.0");
		this.out.writeStartElement("results");
		
		this.out.writeStartElement("data_values");
	}
	
	public void closeWriter() throws XMLStreamException { //Deprecated
		this.out.writeEndElement(); //To end data_values;
		this.out.writeCharacters("\n");
		this.out.writeEndElement(); //To end results;
		this.out.writeCharacters("\n");
		this.out.writeEndDocument();
		
		this.out.close();
	}
	
	public void appendToFile(Coordinates coord) throws XMLStreamException { //Deprecated
		this.out.writeStartElement("data");
		
		this.out.writeStartElement("x");
		this.out.writeCharacters(Double.toString(coord.getx()));
		this.out.writeEndElement();
		
		this.out.writeStartElement("y");
		this.out.writeCharacters(Double.toString(coord.gety()));
		this.out.writeEndElement();
		
		this.out.writeStartElement("z");
		this.out.writeCharacters(Double.toString(coord.getz()));
		this.out.writeEndElement();
		
		this.out.writeEndElement(); //To end data;
		this.out.flush();
	}*/
}

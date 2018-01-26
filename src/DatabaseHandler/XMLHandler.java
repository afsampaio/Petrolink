package DatabaseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import PetroLink.Coordinates;

public class XMLHandler {
	private String fileName;
	private Document doc;
	private Element rootElement, dataValuesElement, dataElement;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	Element current;
	
	public XMLHandler(String file) {
		this.fileName = file;
	}
	
	public void setupWriterDom() throws ParserConfigurationException {
		this.dbf = DocumentBuilderFactory.newInstance();
		this.db = dbf.newDocumentBuilder();
		this.doc = db.newDocument();
		
		Element rootElement = doc.createElement("results");
		doc.appendChild(rootElement);
		
		Element dataValues = doc.createElement("data_values");
		rootElement.appendChild(dataValues);
		current = dataValues;
	}
	
	public void appendToFileDom(Coordinates coord) throws XMLStreamException {
		Element data = this.doc.createElement("data");
		current.appendChild(data);
		
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
		StreamResult result = new StreamResult(new File(this.fileName));
		transformer.transform(source, result);
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

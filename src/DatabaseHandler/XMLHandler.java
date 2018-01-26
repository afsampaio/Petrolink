package DatabaseHandler;

import java.io.File;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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
	private Element current;
	
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
}

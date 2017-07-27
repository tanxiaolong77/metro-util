package com.quanjing.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XmlHelper {
	
	public static Document create() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
        }
        Document doc = dBuilder.newDocument();
        Element rootElement = doc.createElement("xml");
        doc.appendChild(rootElement);
        return doc;
    }

    public static Element rootElement(Document doc) {
        Node node = doc.getFirstChild();
        if (! (node instanceof Element)) {
            return null;
        }

        return (Element) node;
    }

    public static String documentToStr(Document doc) {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            return "";
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        try {
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
        } catch (TransformerException e) {
            return "";
        }
        return sw.toString().replaceAll("\r\n", "\n");
    }

    public static void addElement(Document doc, String key, String value) {
        Element root = rootElement(doc);
        Element e = doc.createElement(key);
        Text t = doc.createTextNode(value);
        e.appendChild(t);
        root.appendChild(e);
    }

    public static String mapToXmlStr(Map<String, String> map) {
        Document d = create();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            addElement(d, entry.getKey(), entry.getValue());
        }

        return documentToStr(d);
    }

    public static Map<String, String> parseXml(String xmlStr) {
        xmlStr = xmlStr.replaceAll("\n", "");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlStr.getBytes("utf-8")));
            Element rootElement = rootElement(document);
            if (rootElement == null) {
                return new HashMap<>();
            }
            Node node = rootElement.getFirstChild();
            if (! (node instanceof Element)) {
                return new HashMap<>();
            }

            Element iterator = (Element) node;
            Map<String, String> ret = new HashMap<>();
            while (iterator != null) {
                node = iterator.getFirstChild();
                if (! (node instanceof Text)) {
                    return new HashMap<>();
                }
                String value = node.getNodeValue();
                String key = iterator.getNodeName();
                ret.put(key, value);

                node = iterator.getNextSibling();
                if (node == null) {
                    return ret;
                }

                if (! (node instanceof Element)) {
                    return new HashMap<>();
                }

                iterator = (Element) node;
            }

            return ret;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }
    
}

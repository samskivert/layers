

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.parsers.ParserConfigurationException;

// import org.w3c.dom.Document;
// import org.w3c.dom.Element;
// import org.w3c.dom.NodeList;
// import org.xml.sax.SAXException;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.DocumentException;

public class DomParserDom4j {

    //No generics
    List myEmpls;
    Document dom;


    public DomParserDom4j(){
        //create a list to hold the employee objects
        myEmpls = new ArrayList();
    }

    public void runExample() {
		
        //parse the xml file and get the dom object
        parseXmlFile();
		
        //get each employee element and create a Employee object
        parseDocument();
		
        //Iterate through the list and print the data
        printData();
		
    }
	
	
    private void parseXmlFile(){
        //get the factory
            
        SAXReader reader = new SAXReader();
        //DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
        try {
			
            //Using factory get an instance of document builder
            //DocumentBuilder db = dbf.newDocumentBuilder();
			
            //parse using builder to get DOM representation of the XML file
            dom = reader.read("employees.xml");
			

        }catch(DocumentException e) {
            e.printStackTrace();
        }
    }

	
    private void parseDocument(){
        //get the root elememt
        Element docEle = dom.getRootElement();
		
        //get a nodelist of <employee> elements
        List nl = docEle.elements("Employee");
        if(nl != null && nl.size() > 0) {
            for(int i = 0 ; i < nl.size();i++) {
				
                //get the employee element
                Element el = (Element)nl.get(i);

                List l = el.attributes();
                System.out.println(getValue(l, "type"));

                //get the Employee object
                Employee e = getEmployee(el);
				
                //add it to list
                myEmpls.add(e);
            }
        }
    }

    private String getValue(List l, String name) {
        for (Object o : l) {
            Attribute attr = (Attribute)o;
            if (attr.getQualifiedName().equals(name))
                return attr.getValue();
        }
        return "";
    }


    /**
     * I take an employee element and read the values in, create
     * an Employee object and return it
     * @param empEl
     * @return
     */
    private Employee getEmployee(Element empEl) {
		
        //for each <employee> element get text or int values of 
        //name ,id, age and name
        String name = getTextValue(empEl,"Name");
        int id = getIntValue(empEl,"Id");
        int age = getIntValue(empEl,"Age");

        String type = empEl.attributeValue("type");
		
        //Create a new Employee with the value read from the xml nodes
        Employee e = new Employee(name,id,age,type);
		
        return e;
    }


    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content 
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is name I will return John  
     * @param ele
     * @param tagName
     * @return
     */
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        List nl = ele.elements(tagName);
        if(nl != null && nl.size() > 0) {
            Element el = (Element)nl.get(0);
            textVal = el.getText();
        }

        return textVal;
    }

	
    /**
     * Calls getTextValue and returns a int value
     * @param ele
     * @param tagName
     * @return
     */
    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele,tagName));
    }
	
    /**
     * Iterate through the list and print the 
     * content to console
     */
    private void printData(){
		
        System.out.println("No of Employees '" + myEmpls.size() + "'.");
		
        Iterator it = myEmpls.iterator();
        while(it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

	
    public static void main(String[] args){
        //create an instance
        DomParserDom4j dpe = new DomParserDom4j();
		
        //call run example
        dpe.runExample();
    }

}

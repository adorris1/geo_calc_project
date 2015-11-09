import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
//import GeocodingApiRequest;
//import build.filtered.src.main.java.com.google.maps.GeoCodingApiRequest;
import build.filtered.src.main.java.com.google.maps.GeocodingApiRequest;
import build.filtered.src.main.java.com.google.maps.model.GeocodingResult;
import build.filtered.src.main.java.com.google.maps.GeoApiContext;
import build.filtered.src.main.java.com.google.maps.GeocodingApi;
/**
 * Creates a connection with google maps and uses a an example address
 * for the lat/long retreival 
 * 
 * @author Ashley Dorris
 * 
 * */
 
public class LongLatService {

    private static final String GEOCODE_REQUEST_URL = "http://maps.googleapis.com/maps/api/geocode/xml?sensor=false&";
    private static HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
     
    public static void main(String[] args) throws Exception{
    	GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyDpCee8Tas3CDrzu88nFA-KEw85d5Kcbtc");
    	
        LongLatService tDirectionService = new LongLatService();
        tDirectionService.getLongitudeLatitude("Rijnsburgstraat 9-11, Amsterdam, The Netherlands");
    }
    
    /**
     * GeoApiContext context = new GeoApiContext().setApiKey("AIza...");
GeocodingResult[] results =  GeocodingApi.geocode(context,
    "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
System.out.println(results[0].formattedAddress);
     * Method getLongitudeLatidue uses connection to google maps to 
     * retreive lat and long values. Parses string paramter which 
     * holds address and passes values to GeoCoder 
     * 
     * @author Ashley Dorris
     * @param  address - a string value holding an address
     * */
    public void getLongitudeLatitude(String address) {
        try {
            StringBuilder urlBuilder = new StringBuilder(GEOCODE_REQUEST_URL);
            if (StringUtils.isNotBlank(address)) {
                urlBuilder.append("&address=").append(URLEncoder.encode(address, "UTF-8"));
            }
 
            final GetMethod getMethod = new GetMethod(urlBuilder.toString());
            try {
                httpClient.executeMethod(getMethod);
                Reader reader = new InputStreamReader(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet());
                 
                int data = reader.read();
                char[] buffer = new char[1024];
                Writer writer = new StringWriter();
                while ((data = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, data);
                }
 
                String result = writer.toString();
                System.out.println(result.toString());
 
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader("<"+writer.toString().trim()));
                Document doc = db.parse(is);
             
                String strLatitude = getXpathValue(doc, "//GeocodeResponse/result/geometry/location/lat/text()");
                System.out.println("Latitude:" + strLatitude);
                 
                String strLongtitude = getXpathValue(doc,"//GeocodeResponse/result/geometry/location/lng/text()");
                System.out.println("Longitude:" + strLongtitude);
                 
                 
            } finally {
                getMethod.releaseConnection();
            }
        } catch (Exception e) {
             e.printStackTrace();
        }
    }
    /**
     * Method getXPathValue uses XPathFactory to parse the result 
     * and returns value.  
     * 
     * @author Ashley Dorris
     * @param  strXpath - string values with holds the Xpath
     * @param  Doc holds the document with address
     * @return resultData - hold the nodes with address results 
     * */
    private String getXpathValue(Document doc, String strXpath) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xPath.compile(strXpath);
        String resultData = null;
        Object result4 = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result4;
        for (int i = 0; i < nodes.getLength(); i++) {
            resultData = nodes.item(i).getNodeValue();
        }
        return resultData;
    }
     
}
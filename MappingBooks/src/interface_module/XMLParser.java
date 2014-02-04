package interface_module;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

 public class XMLParser
 {
	 
	 public List<Words> XMLTag(String text){
		 List <Words> l1 = new LinkedList<Words>();
		 LinkedHashMap<String,Words> h = new LinkedHashMap<String,Words>();
		 try{
			 
			 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	         factory.setNamespaceAware(true);
	         XmlPullParser xpp = factory.newPullParser();

	         xpp.setInput(new StringReader (text));
	         int eventType = xpp.getEventType();
	         
	        String id = "";
	         while (eventType != XmlPullParser.END_DOCUMENT) {
	            String name = xpp.getName();
	            if(name != null && eventType == XmlPullParser.START_TAG && name.equalsIgnoreCase("w")){
		   	            int no = xpp.getAttributeCount() - 2;
		   	            if(no > 0){
		   	                String atr = xpp.getAttributeName(no);
		   	                if(atr != null && atr.equalsIgnoreCase("id"))
		   	                	id = xpp.getAttributeValue(no);//Float.parseFloat(xpp.getAttributeValue(no)); 
		   	                
		   	            
		   	                while(eventType != XmlPullParser.TEXT)
		   	                	eventType = xpp.next();
		   	                Words w = new Words();
		   	                w.isEntity = false;
		   	                w.word = xpp.getText();
		   	                if(w.word.equals("mai"))
		   	                	System.out.println("aici");
		   	                h.put(id, w);
		   	            }
	            	}
	            if(name != null && eventType == XmlPullParser.START_TAG && name.equalsIgnoreCase("entity")){
	            	String atr = xpp.getAttributeName(1);
   	                if(atr != null && atr.equalsIgnoreCase("ref"))
   	                	id = xpp.getAttributeValue(1);//id = Float.parseFloat(xpp.getAttributeValue(1));
   	                Words w = h.get(id);
   	                w.isEntity = true;
   	                h.put(id, w);
	            	
	            }
	            eventType = xpp.next();
	         }
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 
		 }
	//	 String str = h.get(1.1).word;
		 for (LinkedHashMap.Entry<String, Words> entry : h.entrySet()) { 
			    Words value = entry.getValue();
			    l1.add(value);
			}
		 return l1;
	 }
		 
		 
	 public List<Segment> XMLTagParser(String text){
		 List <Segment> l = new LinkedList<Segment>();
		 try{
			 
			 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	         factory.setNamespaceAware(true);
	         XmlPullParser xpp = factory.newPullParser();

	         xpp.setInput(new StringReader (text));
	         int eventType = xpp.getEventType();
	         
	        
	         while (eventType != XmlPullParser.END_DOCUMENT) {
	   	            int attributeCount = xpp.getAttributeCount();
	   	            Segment ob = new Segment();
	   	            ob.contains = false;
	   	            for(int i = 0; i < attributeCount; i++){
	   	                String name = xpp.getAttributeName(i);
	   	                ob.contains = true;
	   	                if(name != null && name.equalsIgnoreCase("begin")){
	   	                	ob.start = xpp.getAttributeValue(i);               
	   	                }
	   	                if(name != null && name.equalsIgnoreCase("end")){
	   	                	ob.end = xpp.getAttributeValue(i);             
	   	                }
	   	                if(name != null && name.equalsIgnoreCase("distance")){
	   	                	ob.distance = (xpp.getAttributeValue(i));              
	   	                }
	   	            }
	   	            if(ob.contains == true)
	   	            	l.add(ob);
	   	            for(int i = 1; i < attributeCount; i++)
	   	            	eventType = xpp.next();
	                  eventType = xpp.next(); 

	              }

			 
		 }catch(IOException e){
			 Log.d("Error", "Error Message: " + e.getMessage());
		 }
		 catch (XmlPullParserException e){
			 Log.d("Error", "Error Message: " + e.getMessage());
		 }
		 return l;
	 }
 }
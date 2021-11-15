package it.kdm.doctoolkit.services;

import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.TracerServiceStub;
import it.kdm.doctoolkit.exception.DocerApiException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TracerService {
//	 public static void setWSURL(String url) {
//	        ClientManager.INSTANCE.setTracerEpr(url);
//	 }
	 public static TracerServiceStub.Trace[] recuperaCronologia(String token,Long docNum)  throws DocerApiException{
		 return recuperaCronologia(token,docNum,null,null,null);
	 }
	 public static TracerServiceStub.Trace[] recuperaCronologia(String token,Long docNum, String extraData,Calendar dateFrom, Calendar dateTo)  throws DocerApiException{
    	 
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    	TracerServiceStub.ReadResponse gur = null;
	    	TracerServiceStub.Read gui = new TracerServiceStub.Read();
	    	try {
		    	if(extraData==null) extraData = "";
		    	if(dateFrom==null){
		    		Calendar calendarFrom = Calendar.getInstance();
		    		calendarFrom.setTime(sdf.parse("1900-01-01"));
		    		dateFrom = calendarFrom;
		    	}
		    	if(dateTo==null){
		    		Calendar calendarTo = Calendar.getInstance();
		    		calendarTo.setTime(sdf.parse("3000-01-01"));
		    		dateTo = calendarTo;
		    	}
		    	gui.setDocnum(Long.toString(docNum));
		    	gui.setExtradata(extraData);
		    	gui.setFrom(dateFrom);
		    	gui.setTo(dateTo);
	    		gui.setToken(token);
                gui.setUser("");
                gui.setOptype("");

                String sede = ToolkitConnector.extractSedeFromToken(token);
	    		TracerServiceStub serv = ClientManager.INSTANCE.getTracerClient(sede);
	    		gur =  serv.read(gui);
		
			} catch (Exception e) {
				throw new DocerApiException(e);
			}
	    	return gur.get_return();
	    	 
	  }
}

package utils;

import java.util.HashMap;
import java.util.Map;

import models.WXTicket;

import play.cache.Cache;

public class WXTicketCache {
	private static WXTicketCache ticketCache;  
	
    private WXTicketCache (){}  

    public static WXTicketCache getInstance() {  
	    if (ticketCache == null) {  
	    	ticketCache = new WXTicketCache();  
	    }  
	    return ticketCache;  
    }  
        

    public void setWXTicketCache(WXTicket ticket){
        if(ticket != null){
        	Cache.set(WXTicketUtil.TICKET_KEY, ticket, "60mn");
        }
    }

    public WXTicket getWXTicketCache(){
        return Cache.get(WXTicketUtil.TICKET_KEY, WXTicket.class);
    }
}

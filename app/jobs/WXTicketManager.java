package jobs;

import java.io.IOException;
import java.util.Date;

import models.WXTicket;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.CommonUtil;
import utils.WXTicketCache;
import utils.WXTicketUtil;

@OnApplicationStart
@Every("15mn")
public class WXTicketManager extends Job {
	
	public void doJob() {
		WXTicketCache ticketCache= WXTicketCache.getInstance();
        WXTicket ticket = ticketCache.getWXTicketCache();
        try {
        	if(ticket != null && !CommonUtil.isBlank(ticket.getTicket())){
                Long timeGap = (new Date().getTime() - ticket.getGetTicketDatetime().getTime()) / 1000;
                
                if ((7200-timeGap) < 900) {
                	ticket = WXTicketUtil.getWXTicket();
                    ticketCache.setWXTicketCache(ticket);
                }
            }else{
    			ticket = WXTicketUtil.getWXTicket();
                ticketCache.setWXTicketCache(ticket);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}

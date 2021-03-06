package com.sapient.metallica.message;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.sapient.metallica.beans.Commodity;
import com.sapient.metallica.beans.CounterParty;
import com.sapient.metallica.beans.Location;
import com.sapient.metallica.beans.Side;
import com.sapient.metallica.beans.Trade;
import com.sapient.metallica.beans.TradeVO;
import com.sapient.metallica.repository.TradeRepository;
import com.sapient.metallica.util.MetallicaConstants;
import com.sapient.metallica.util.MetallicaUtil;

public class TradeService {
	@Autowired
	private TradeRepository repository;
	
	
	
	private final RabbitTemplate rabbitTemplate;
	
	private final TopicExchange exchange;

	  public TradeService(RabbitTemplate rabbitTemplate, TopicExchange exchange) {
	    this.rabbitTemplate = rabbitTemplate;
	    this.exchange = exchange;
	  }

	 

	public void createTrade(TradeVO dto) {
	   
	    
		Trade trade = new Trade();
		trade.setCommodity(new Commodity(dto.getCommodity()));
		trade.setCounterParty(new CounterParty(dto.getCounterParty()));
		trade.setLocation(new Location(dto.getLocation()));
		trade.setPrice(dto.getPrice());
		trade.setQuanity(dto.getQuanity());
		trade.setSide(Side.valueOf(dto.getSide()));
		trade.setTradeDate(MetallicaUtil.parseDate(dto.getDate(), MetallicaConstants.DD_MM_YY));
		repository.save(trade);
		
		String routingKey = "trade.created";	   
	    rabbitTemplate.convertAndSend(exchange.getName(), routingKey, trade.getCommodity());
	  }

}


package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  public TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
      throws JsonProcessingException {
    ObjectMapper objectMapper = getObjectMapper();
    String uri = buildUri(symbol, from, to);
    String result = (restTemplate.getForObject(uri, String.class));
    List<TiingoCandle> candles = objectMapper.readValue(result, 
        new TypeReference<List<TiingoCandle>>() {});
    List<Candle> candleList = new ArrayList<>();
    for (int i = 0; i < candles.size();i++) {
      candleList.add(candles.get(i));
    }

    return candleList;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?" + "startDate=" + startDate + "&endDate="
        + endDate + "&token=6d7fd8b1cb1df3582780c7017db377f977642d71";
    return uriTemplate;
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Now we will be separating communication with Tiingo from PortfolioManager.
  //  Generate the functions as per the declarations in the interface and then
  //  Move the code from PortfolioManagerImpl#getSTockQuotes inside newly created method.
  //  Run the tests using command below -
  //  ./gradlew test --tests TiingoServiceTest and make sure it passes.



  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call tiingo service.

}

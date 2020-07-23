
package com.crio.warmup.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageDailyResponse {

  @JsonProperty(value = "Time Series (Daily)")
  private Map<LocalDate, AlphavantageCandle> candles;

  public Map<LocalDate, AlphavantageCandle> getCandles() {
    return candles;
  }
  

  public void setCandles(
      TreeMap<LocalDate, AlphavantageCandle> candles) {
    this.candles = candles;
  }

  public AlphavantageDailyResponse() {
  }

  public AlphavantageDailyResponse(TreeMap<LocalDate, AlphavantageCandle> candles) {
    this.candles = candles;
  }
  
}

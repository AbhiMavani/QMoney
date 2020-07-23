package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;

import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.crio.warmup.stock.portfolio.PortfolioManagerImpl;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File fi = resolveFileFromResources(args[0]);
    ObjectMapper mapper = getObjectMapper();
    JsonFactory factory = new JsonFactory();
    JsonParser jp = factory.createJsonParser(fi);
    jp.setCodec(mapper);
    JsonNode jn = jp.readValueAsTree();
    final List<String> stockList = jn.findValuesAsText("symbol");
    return stockList;
  }

  public static List<String> mainReadQuotes(String[] args) 
      throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> allJsonObjects = objectMapper.readValue(file, new 
        TypeReference<List<PortfolioTrade>>() {});
    List<String> allSymbols = new ArrayList<String>();
    List<TotalReturnsDto> mappingList = getSortedClosingPrice(objectMapper, 
        allJsonObjects, args);
    Collections.sort(mappingList);
    for (TotalReturnsDto trDto : mappingList) {
      allSymbols.add(trDto.getSymbol());
      System.out.println(trDto.getClosingPrice());
    }
    // System.out.print(allSymbols);
    return allSymbols;
  }

  public static List<TotalReturnsDto> getSortedClosingPrice(ObjectMapper objectMapper,
      List<PortfolioTrade> allJsonObjects, String[] args) throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate();
    List<TotalReturnsDto> mappingList = new ArrayList<TotalReturnsDto>();
    for (PortfolioTrade obj : allJsonObjects) {
      String uri = "https://api.tiingo.com/tiingo/daily/" + obj.getSymbol() + "/prices?startDate="
          + obj.getPurchaseDate() + "&endDate=" + args[1]  
            + "&token=6d7fd8b1cb1df3582780c7017db377f977642d71";
      String result = (restTemplate.getForObject(uri, String.class));
      List<TiingoCandle> candleList = objectMapper.readValue(result, new 
          TypeReference<List<TiingoCandle>>() {});;
      TiingoCandle candleObj = candleList.get(candleList.size() - 1);
      TotalReturnsDto trDto = new TotalReturnsDto(obj.getSymbol(), candleObj.getClose());
      mappingList.add(trDto);
      // System.out.println(obj.getSymbol()+candleObj);
    }
    return mappingList;
  }

  public static List<TotalReturnsDto> getSortedOpeningPrice(ObjectMapper objectMapper,
      List<PortfolioTrade> allJsonObjects, String[] args) throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate();
    List<TotalReturnsDto> mappingList = new ArrayList<TotalReturnsDto>();
    for (PortfolioTrade obj : allJsonObjects) {
      String uri = "https://api.tiingo.com/tiingo/daily/" + obj.getSymbol() + "/prices?startDate="
          + obj.getPurchaseDate() + "&endDate=" + args[1] 
            + "&token=6d7fd8b1cb1df3582780c7017db377f977642d71";
      String result = (restTemplate.getForObject(uri, String.class));
      List<TiingoCandle> candleList = objectMapper.readValue(result, new 
          TypeReference<List<TiingoCandle>>() {});;
      TiingoCandle candleObj = candleList.get(0);
      TotalReturnsDto trDto = new TotalReturnsDto(obj.getSymbol(), candleObj.getOpen());
      mappingList.add(trDto);
      // System.out.println(obj.getSymbol()+candleObj);
    }
    return mappingList;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Copy the relevant code from #mainReadQuotes to parse the Json into
  // PortfolioTrade list and
  // Get the latest quotes from TIingo.
  // Now That you have the list of PortfolioTrade And their data,
  // With this data, Calculate annualized returns for the stocks provided in the
  // Json
  // Below are the values to be considered for calculations.
  // buy_price = open_price on purchase_date and sell_value = close_price on
  // end_date
  // startDate and endDate are already calculated in module2
  // using the function you just wrote #calculateAnnualizedReturns
  // Return the list of AnnualizedReturns sorted by annualizedReturns in
  // descending order.
  // use gralde command like below to test your code
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // where trades.json is your json file
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) 
      throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> allJsonObjects = objectMapper.readValue(file, new 
        TypeReference<List<PortfolioTrade>>() {});
    List<TotalReturnsDto> mappingClose = getSortedClosingPrice(objectMapper, 
        allJsonObjects, args);
    List<TotalReturnsDto> mappingOpen = getSortedOpeningPrice(objectMapper, 
        allJsonObjects, args);
    List<AnnualizedReturn> anList = new ArrayList<>();

    for (int i = 0; i < allJsonObjects.size(); i++) {
      AnnualizedReturn an = calculateAnnualizedReturns(LocalDate.parse(args[1]), 
          allJsonObjects.get(i),
          mappingOpen.get(i).getClosingPrice(), mappingClose.get(i)
          .getClosingPrice());
      anList.add(an);
    }
    Collections.sort(anList);
    return anList;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // annualized returns should be calculated in two steps -
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  // Store the same as totalReturns
  // 2. calculate extrapolated annualized returns by scaling the same in years
  // span. The formula is
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // Store the same as annualized_returns
  // return the populated list of AnnualizedReturn for all stocks,
  // Test the same using below specified command. The build should be successful
  // ./gradlew test --tests
  // PortfolioManagerApplicationTest.testCalculateAnnualizedReturn
  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, 
      PortfolioTrade trade, 
        Double buyPrice, Double sellPrice) {
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    LocalDate startDate = trade.getPurchaseDate();
    long days = ChronoUnit.DAYS.between(startDate, endDate);
    Double year = Double.valueOf(days);
    // System.out.println(year);
    Double annualizedReturns = Math.pow((1 + totalReturn), (365 / year)) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturn);
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class
        .getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader()
        .getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/kevaltalaviya122-ME_QMONEY/"
        + "qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@5a9d6f02";
    String functionNameFromTestFileInStackTrace = "mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, 
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace, 
        lineNumberFromTestFileInStackTrace });
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory,
  //  Create PortfolioManager using PortfoliomanagerFactory,
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.
  //  Test the same using the same commands as you used in module 3
  //  use gralde command like below to test your code
  //  ./gradlew run --args="trades.json 2020-01-01"
  //  ./gradlew run --args="trades.json 2019-07-01"
  //  ./gradlew run --args="trades.json 2019-12-03"
  //  where trades.json is your json file
  //  Confirm that you are getting same results as in Module3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args) 
      throws Exception {
    String file = args[0];
    RestTemplate rest = new RestTemplate();
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(rest);
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    File file1  = resolveFileFromResources(contents);
    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> portfolioTrades = objectMapper.readValue(file1, new 
        TypeReference<List<PortfolioTrade>>() {});;
    return portfolioManager.calculateAnnualizedReturn(portfolioTrades, endDate);
  }

  private static String readFileAsString(String filename) throws IOException, URISyntaxException {
    return filename;
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainCalculateReturnsAfterRefactor(args));

  }
}

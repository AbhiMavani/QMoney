# QMoney
QMoney is a PortfolioManager Application in which you can handle your stocks for your portfolio.

Its a Java application in which using stocks data gets by calling API.
Application devloped using Design patterns (Absract Factory) so it will clear your fundamentals for design patterns beacuse it's real time example. 

In this application Tingo API and Alphadvantage API is used for getting Stocks data of various company.
Use Spring Freamwork in which RestTemplate for gets the data and ObjectMapper and fastxml for mapping data into json and jason data to java POJO class. 
QMoney is a visual stock portfolio analyzer. It helps portfolio managers make trade recommendations for their clients.

During this Micro-Experience, I:

Implemented the core logic of the portfolio manager and published it as a library.

Refactored code to add support for multiple stock quote services.

Improved application stability and performance.
QMoney Architecture

Basic Concept Image


QMoney Portfolio Manager Interface

Basic Concept Image

QMoney Modules

Fetch stock quotes and compute annualized stock returns

Scope of work:

1. Used Tiingo’s REST APIs to fetch stock quotes.
2. Computed the annualized returns based on stock purchase date and holding period.
Skills Learned:

Java, REST API, Jackson
Refactor using Java interfaces and publish a JAR file

Scope of work:

1. Refactored code to adapt to an updated interface contract published by the backend team.
2. Published the portfolio manager library as a JAR for easy versioning and distribution.
3. Created examples to help document library (JAR) usage.

Skills Learned:

Interfaces, Code Refactoring, Gradle
Improve application availability and stability

Scope of work:

1. Added support for a backup stock quote service (Alpha Vantage) to improve service availability.
2. Improved application stability with comprehensive error reporting and better exception handling.

Skills Learned:

Interfaces, Exception Handling

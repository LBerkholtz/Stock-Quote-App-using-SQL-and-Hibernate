package lberkholtz.advancedjava.assignmentfive.services;

import lberkholtz.advancedjava.assignmentfive.model.StockData;
import lberkholtz.advancedjava.assignmentfive.model.StockQuote;
import lberkholtz.advancedjava.assignmentfive.util.DatabaseUtils;
import lberkholtz.advancedjava.assignmentfive.services.DatabaseStockService;
import lberkholtz.advancedjava.assignmentfive.util.DatabaseInitializationException;

import lberkholtz.advancedjava.assignmentfive.services.Interval;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Unit tests for the DatabaseStockService
 */
public class DatabaseStockServiceTest extends StockData {

    private DatabaseStockService databaseStockService;

    @Before
    public void setUp() throws DatabaseInitializationException {
        DatabaseUtils.initializeDatabase(DatabaseUtils.initializationFile);
        databaseStockService = new DatabaseStockService();

    }

    @Test
    public void testGetQuote() throws Exception {
        DatabaseStockService databaseStockService = new DatabaseStockService();
        String symbol = "APPL";
        StockQuote stockQuote = databaseStockService.getQuote(symbol);
        assertNotNull("Verify we can get a stock quote from the db", stockQuote);
        assertEquals("Make sure the symbols match", symbol, stockQuote.getSymbol());
    }

    @Test
    public void testGetQuoteList() throws Exception {
        DatabaseStockService databaseStockService = new DatabaseStockService();
        List<StockQuote> stockQuotes = null;
        String symbol = "GOOG";
        String dteFrom = "2004/01/01";
        String dteUntil = "2015/05/01";
        Calendar from = Calendar.getInstance();
        Calendar until = Calendar.getInstance();
        from.setTime(simpleDateFormat.parse(dteFrom));
        until.setTime(simpleDateFormat.parse(dteUntil));
        Interval interval = Interval.DAILY;
        stockQuotes  = databaseStockService.getQuote(symbol, from, until, Interval.DAILY);
        assertEquals("verify number of quotes",2, stockQuotes.size());
    }


    @Test
    public void testGetQuoteWithinRange() throws Exception {

        String fromDateString = "2015-02-10 00:01:01";
        String endDateString = "2015-02-10 01:08:01";
        String symbol = "AMZN";

        String sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','" + fromDateString + "','363.21');";
        DatabaseUtils.executeSQL(sql);

        sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','2015-02-10 00:04:01','250.21');";
        DatabaseUtils.executeSQL(sql);

        sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','2015-02-10 00:06:01','251.21');";
        DatabaseUtils.executeSQL(sql);

        sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','" + endDateString + "','253.21');";
        DatabaseUtils.executeSQL(sql);

        Calendar fromCalendar = makeCalendarFromString(fromDateString);
        Calendar untilCalendar = makeCalendarFromString(endDateString);

        List<StockQuote> stockQuotes = databaseStockService.getQuote(symbol, fromCalendar, untilCalendar, Interval.DAILY);
        assertEquals("got back expected number of stockquotes for one day interval", 1, stockQuotes.size());

        stockQuotes = databaseStockService.getQuote(symbol, fromCalendar, untilCalendar, Interval.HOURLY);
        assertEquals("got back expected number of stockquotes for one minute interval", 2, stockQuotes.size());
    }

    /**
     * Handy dandy helper method that converts Strings in the format of   StockData.dateFormat
     * to Calendar instances set to the date expressed in the string.
     *
     * @param dateString a data and time in this format: StockData.dateFormat
     * @return a calendar instance set to the time in the string.
     * @throws ParseException if the string is not in the correct format, we can't tell what
     *                        time it is, and therefore can't make a calender set to that time.
     */
    private Calendar makeCalendarFromString(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat(StockData.dateFormat, Locale.ENGLISH);
        Date date = format.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

}



package org.cshaifa.spring.entities;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import net.bytebuddy.asm.Advice;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {

    private ReportType reportType;

    private Store store;

    private LocalDate startDate;

    private LocalDate endDate;

    Map<LocalDate, Integer> getDailyOrderValues(List<Order> orders) {
        Map<LocalDate, Integer> dailyValues = new HashMap<>();

        for (Order order : orders) {
            LocalDate orderDate = order.getOrderDate().toLocalDateTime().toLocalDate();

            if (orderDate.isAfter(startDate.minusDays(1)) && orderDate.isBefore(endDate)) {
                if (dailyValues.containsKey(orderDate)) {
                    Integer val = dailyValues.get(orderDate);
                    dailyValues.put(orderDate, ++val);
                } else {
                    dailyValues.put(orderDate, 1);
                }
            }
        }

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1))
        {
            if (!dailyValues.containsKey(date)) {
                dailyValues.put(date, 0);
            }
        }

        return dailyValues;
    }


    Map<LocalDate, Integer> getDailyRevenueValues(List<Order> orders) {
        Map<LocalDate, Integer> dailyValues = new HashMap<>();

        for (Order order : orders) {
            LocalDate orderDate = order.getOrderDate().toLocalDateTime().toLocalDate();
            int dailyRevenue = (int) order.getTotal();
            if (orderDate.isAfter(startDate.minusDays(1)) && orderDate.isBefore(endDate)) {
                if (dailyValues.containsKey(orderDate)) {
                    int val = dailyValues.get(orderDate);
                    dailyValues.put(orderDate, val+dailyRevenue);
                } else {
                    dailyValues.put(orderDate, dailyRevenue);
                }
            }
        }

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1))
        {
            if (!dailyValues.containsKey(date)) {
                dailyValues.put(date, 0);
            }
        }

        return dailyValues;
    }

    Map<LocalDate, Integer> getDailyComplaints(List<Complaint> complaints) {
        Map<LocalDate, Integer> dailyValues = new HashMap<>();

        for (Complaint complaint : complaints) {
            LocalDate orderDate = complaint.getComplaintTimestamp().toLocalDateTime().toLocalDate();
            if (orderDate.isAfter(startDate.minusDays(1)) && orderDate.isBefore(endDate)) {
                if (dailyValues.containsKey(orderDate)) {
                    Integer val = dailyValues.get(orderDate);
                    dailyValues.put(orderDate, ++val);
                } else {
                    dailyValues.put(orderDate, 0);
                }
            }
        }

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1))
        {
            if (!dailyValues.containsKey(date)) {
                dailyValues.put(date, 0);
            }
        }

        return dailyValues;
    }

    public XYDataset createDataset(Map<LocalDate, Integer> dailyValues) {

        ZoneId defaultZoneId = ZoneId.systemDefault();

        final TimePeriodValues series = new TimePeriodValues("Days");

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1))
        {
            Date start = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
            Date end = Date.from(date.plusDays(1).atStartOfDay(defaultZoneId).toInstant());
            series.add(new SimpleTimePeriod(start, end), dailyValues.get(date));
        }

        final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        dataset.addSeries(series);
        //dataset.setDomainIsPointsInTime(false);

        return dataset;
    }

    public Report(ReportType reportType, Store store, LocalDate startDate, LocalDate endDate) {
        this.reportType = reportType;
        this.store = store;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Report() {
    }

    public void generateHistogram() {
        if (store == null) {
            // Generate histogram for entire chain
        }
        else {
            // Generate histogram for store
        }
        XYDataset dataset = null;
        if(reportType== ReportType.ORDERS){ //numbers of orders per date
            List<Order> storeOrders = store.getOrders();
            Map<LocalDate, Integer> dailyValues = getDailyOrderValues(storeOrders);
            dataset = createDataset(dailyValues);
        }
        else if(reportType== ReportType.COMPLAINTS){
            List<Complaint> complaintList = store.getComplaints();
            Map<LocalDate, Integer> dailyValues = getDailyComplaints(complaintList);
            dataset = createDataset(dailyValues);
        }
        else{//reportType== ReportType.REVENUE
            List<Order> storeOrders = store.getOrders();
            Map<LocalDate, Integer> dailyValues = getDailyRevenueValues(storeOrders);
            dataset = createDataset(dailyValues);
        }

        //super(title);

        final XYItemRenderer renderer1 = new XYBarRenderer();

        final DateAxis domainAxis = new DateAxis("Date");
        final ValueAxis rangeAxis = new NumberAxis("Value");

        final XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer1);

        final JFreeChart chart = new JFreeChart("Time Period Values Demo", plot);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);

        //JFreeChart histogram = ChartFactory.createTimeSeriesChart("JFreeChart Histogram", "Time", "Revenue", dataset);

        try {
            String histogramImagePath = "images/histograms/" ;
            File histFile = new File(histogramImagePath+"histogram_"+ startDate.toString() + "_" + endDate.toString() + ".png");
            ChartUtils.saveChartAsPNG(histFile, chart, 500, 270);
        } catch (IOException e) {
            System.out.println("Creating histogram image failed.");
            e.printStackTrace();
        }
    }

}
package org.cshaifa.spring.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Order;
import org.cshaifa.spring.entities.Store;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;

public class Report {

    private ReportType reportType;

    private Store store;

    private LocalDate startDate;

    private LocalDate endDate;

    private byte[] reportImage = null;

    public Report(ReportType reportType, Store store, LocalDate startDate, LocalDate endDate) {
        this.reportType = reportType;
        this.store = store;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Report() {
    }

    public byte[] getReportImage() {
        return reportImage;
    }

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

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
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
                    dailyValues.put(orderDate, val + dailyRevenue);
                } else {
                    dailyValues.put(orderDate, dailyRevenue);
                }
            }
        }
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (!dailyValues.containsKey(date)) {
                dailyValues.put(date, 0);
            }
        }
        return dailyValues;
    }

    Map<LocalDate, Integer> getDailyComplaints(List<Complaint> complaints) {
        Map<LocalDate, Integer> dailyValues = new HashMap<>();
        for (Complaint complaint : complaints) {
            LocalDate complaintDate = complaint.getComplaintTimestamp().toLocalDateTime().toLocalDate();
            if (complaintDate.isAfter(startDate.minusDays(1)) && complaintDate.isBefore(endDate)) {
                if (dailyValues.containsKey(complaintDate)) {
                    int val = dailyValues.get(complaintDate);
                    dailyValues.put(complaintDate, ++val);
                } else {
                    dailyValues.put(complaintDate, 1);
                }
            }
        }
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (!dailyValues.containsKey(date)) {
                dailyValues.put(date, 0);
            }
        }

        return dailyValues;
    }

    public XYDataset createDataset(Map<LocalDate, Integer> dailyValues) {

        ZoneId defaultZoneId = ZoneId.systemDefault();

        final TimePeriodValues series = new TimePeriodValues("Days");

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Date start = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
            Date end = Date.from(date.plusDays(1).atStartOfDay(defaultZoneId).toInstant());
            series.add(new SimpleTimePeriod(start, end), dailyValues.get(date));
        }

        final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        dataset.addSeries(series);
        // dataset.setDomainIsPointsInTime(false);

        return dataset;
    }

    public boolean generateHistogram() {
        XYDataset dataset = null;
        String histTitle = "";
        if (store != null) {
            if (reportType == ReportType.ORDERS) { // numbers of orders per date
                List<Order> storeOrders = store.getOrders();
                Map<LocalDate, Integer> dailyValues = getDailyOrderValues(storeOrders);
                dataset = createDataset(dailyValues);
                histTitle = store.getName() + " Orders Histogram";
            } else if (reportType == ReportType.COMPLAINTS) {
                List<Complaint> complaintList = store.getComplaints();
                Map<LocalDate, Integer> dailyValues = getDailyComplaints(complaintList);
                dataset = createDataset(dailyValues);
                histTitle = store.getName() + " Complaint Histogram";
            } else if (reportType == ReportType.REVENUE) {
                List<Order> storeOrders = store.getOrders();
                Map<LocalDate, Integer> dailyValues = getDailyRevenueValues(storeOrders);
                dataset = createDataset(dailyValues);
                histTitle = store.getName() + " Revenue Histogram";
            }
        }

        // super(title);

        final XYItemRenderer renderer1 = new XYBarRenderer();

        final DateAxis domainAxis = new DateAxis("Date");
        final ValueAxis rangeAxis = new NumberAxis("Value");

        final XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer1);

        final JFreeChart chart = new JFreeChart(histTitle, plot);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 500, 270);
            reportImage = outputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Creating histogram image failed.");
            e.printStackTrace();
            reportImage = null;
        }

        return reportImage != null;

    }

    public boolean generateChainHistogram(List<Store> storeList) {
        XYDataset dataset = null;
        String histTitle = "";
        if (storeList != null) {
            switch (reportType) {
                case ORDERS -> {
                    List<Order> allOrders = new ArrayList<>();
                    for (Store store : storeList) {
                        allOrders.addAll(store.getOrders());
                    }
                    Map<LocalDate, Integer> dailyValues = getDailyOrderValues(allOrders);
                    dataset = createDataset(dailyValues);
                    histTitle = "Lilach Chain Orders Histogram";
                }
                case COMPLAINTS -> {
                    List<Complaint> allComplaints = new ArrayList<>();
                    for (Store store : storeList) {
                        allComplaints.addAll(store.getComplaints());
                    }
                    Map<LocalDate, Integer> dailyValues = getDailyComplaints(allComplaints);
                    dataset = createDataset(dailyValues);
                    histTitle = "Lilach Chain Complaint Histogram";
                }
                case REVENUE -> {
                    List<Order> allOrders = new ArrayList<>();
                    for (Store store : storeList) {
                        allOrders.addAll(store.getOrders());
                    }
                    Map<LocalDate, Integer> dailyValues = getDailyRevenueValues(allOrders);
                    dataset = createDataset(dailyValues);
                    histTitle = "Lilach Chain Revenue Histogram";
                }
            }
        }

        final XYItemRenderer renderer1 = new XYBarRenderer();

        final DateAxis domainAxis = new DateAxis("Date");
        final ValueAxis rangeAxis = new NumberAxis("Value");

        final XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer1);

        final JFreeChart chart = new JFreeChart(histTitle, plot);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 500, 270);
            reportImage = outputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Creating histogram image failed.");
            e.printStackTrace();
            reportImage = null;
        }

        return reportImage != null;

    }

}

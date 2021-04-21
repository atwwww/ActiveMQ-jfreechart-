package org.example.work;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.jms.*;
import javax.swing.*;
import java.awt.*;

/**
 * 消息消费者-画图
 */

public class JMSConsumer_draw {

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String BROKERURL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static void main(String[] args) {

        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer messageProducer;

        connectionFactory = new ActiveMQConnectionFactory(JMSConsumer_draw.USERNAME, JMSConsumer_draw.PASSWORD, JMSConsumer_draw.BROKERURL);

        try {

            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("CalculationService");
            messageProducer = session.createConsumer(destination);
            messageProducer.setMessageListener(new Listener_draw("随机数统计"));

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}


class Listener_draw extends ApplicationFrame implements MessageListener {

//    XYDataset不能直接使用，需要使用TimeSeriesCollection(TimeSeries)来初始化此时只需要在TimeSeries中添加时间和数据就可以了
    TimeSeries series = new TimeSeries("Random Data");
    XYDataset dataset = new TimeSeriesCollection(series);   //可以实时更新数据
    static JLabel show = new JLabel("实时数据");//显示随机数
    static int counter;

    public Listener_draw(String title) {

        //设置框架的名字
        super(title);

        //设置框架的位置
        setLocation(450,250);

        JPanel menu = new JPanel();

        //创建表格
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Computing Test",
                "Seconds",
                "Value",
                dataset,
                true,
                false,
                false);

        //创建面板，将表格添加到面板中
        ChartPanel chartPanel = new ChartPanel(chart);

        //设置面板大小
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));

        //将面板加入Frame
        add(chartPanel,BorderLayout.CENTER);

        //将标签加入面板
        add(show,BorderLayout.SOUTH);

        //new对象时候直接创建并显示图表
        pack();
        setVisible(true);
    }



    private void addData(double value) {
        if(counter == 100){//每绘制一百个数据，清空重新绘制
            series.clear();
            counter = 0;
        }
        show.setText(Double.toString(value));   //将标签值设定为动态数据
        series.add(new Millisecond(), value);   //每次产生一个新的即时对象获取当前的内容，这个对象会被加入到集合时间集合中
        counter++;
    }

    @Override
    public void onMessage(Message message) {//每次执行将数据添加到数据集的操作，实现实时渲染
        try {
            double var = Double.parseDouble(((TextMessage)message).getText());
            addData(var);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}


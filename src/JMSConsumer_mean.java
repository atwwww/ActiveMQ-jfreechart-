package org.example.work;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.math3.stat.StatUtils;

import javax.jms.*;

/**
 * 信息消费者-均值
 */

public class JMSConsumer_mean {

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String BROKERURL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static void main(String[] args) {

        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer messageProducer;

        connectionFactory = new ActiveMQConnectionFactory(JMSConsumer_mean.USERNAME, JMSConsumer_mean.PASSWORD, JMSConsumer_mean.BROKERURL);

        try {

            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("CalculationService");
            messageProducer = session.createConsumer(destination);
            messageProducer.setMessageListener(new Listener_mean());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}


class Listener_mean implements MessageListener {

    private static final int COUNT = 50;
    private static double[] arr = new double[COUNT];
    private static int counter = 0;

    @Override
    public void onMessage(Message message) {
        try {
            double var = Double.parseDouble( ((TextMessage)message).getText() );//接收到的数据转化为double
            arr[counter++] = var;//将每次接收到的信息存在容器中
            if( counter == 50 ){//当接受的数据量达到50的时候,计算均值
                System.out.println("接收到50个数据，数据均值为：" + StatUtils.mean(arr));
                counter = 0;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
package org.example.work;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.math3.stat.StatUtils;

import javax.jms.*;

/**
 * 消息消费者-计算方差
 */

public class JMSConsumer_variance {
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String BROKERURL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static void main(String[] args) {

        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer messageProducer;

        connectionFactory = new ActiveMQConnectionFactory(JMSConsumer_variance.USERNAME, JMSConsumer_variance.PASSWORD, JMSConsumer_variance.BROKERURL);

        try {

            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("CalculationService");
            messageProducer = session.createConsumer(destination);
            messageProducer.setMessageListener(new Listener_variance());

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}


class Listener_variance implements MessageListener {

    private static final int COUNT = 50;
    private static double[] arr = new double[COUNT];
    private static int counter = 0;

    @Override
    public void onMessage(Message message) {
        try {
            double var = Double.parseDouble( ((TextMessage)message).getText() );//接收到的数据转化为double
            arr[counter++] = var;//将每次接收到的信息存在容器中
            if( counter == 50 ){//当接受的数据量达到50的时候,计算方差
                System.out.println("接收到50个数据，数据方差为：" + StatUtils.variance(arr));
                counter = 0;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
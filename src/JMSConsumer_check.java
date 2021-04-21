package org.example.work;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.math3.stat.StatUtils;

import javax.jms.*;

/**
 * 消息消费者-检查坏数据
 */

public class JMSConsumer_check {

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String BROKERURL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static void main(String[] args) {

        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer messageProducer;

        connectionFactory = new ActiveMQConnectionFactory(JMSConsumer_check.USERNAME, JMSConsumer_check.PASSWORD, JMSConsumer_check.BROKERURL);

        try {

            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("CalculationService");
            messageProducer = session.createConsumer(destination);
            messageProducer.setMessageListener(new Listener_check());

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}

class Listener_check implements MessageListener {

    private static final int COUNT = 50;
    private static double[] arr = new double[COUNT];
    private static int counter = 0;

    @Override
    public void onMessage(Message message) {
        try {
            double var = Double.parseDouble( ((TextMessage)message).getText() );//接收到的数据转化为double
            arr[counter++] = var;//将每次接收到的信息存数组
            if( counter == COUNT ){//当接受的数据量达到50的时候,查找有无坏值
                double mean = StatUtils.mean(arr);
                double standard_variance = Math.sqrt(StatUtils.variance(arr,mean)); //计算标准差
                double bottom = mean - 2*standard_variance;
                double top = mean + 2*standard_variance;
                boolean flag = true;
                for(int i = 0; i < COUNT; i++){
                    if(arr[i] <= bottom || arr[i] >= top){
                        if(flag){
                            System.out.println();
                            System.out.print("接收到50个数据，存在坏值： ");
                            flag = false;
                        }
                        System.out.print(arr[i]+" ");
                    }
                }
                System.out.println();
                if(flag){
                    System.out.println("接收到50个数据，没有坏值！！！");
                }
                counter = 0;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

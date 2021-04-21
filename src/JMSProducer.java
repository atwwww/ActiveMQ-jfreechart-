package org.example.work;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import javax.swing.plaf.IconUIResource;

/**
 * 消息生产者
 */
public class JMSProducer {

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String BROKERURL = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static final int COUNT = 1000; //设定生产消息的数量

    public static void main(String[] args) {

        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageProducer messageProducer;

        connectionFactory = new ActiveMQConnectionFactory(JMSProducer.USERNAME,JMSProducer.PASSWORD,JMSProducer.BROKERURL);

        try {

            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
            destination = (Destination) session.createTopic("CalculationService");
            messageProducer = session.createProducer(destination);
            sendMessage(session,messageProducer);

        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void sendMessage(Session session,MessageProducer messageProducer){
        for(int i = 0; i < JMSProducer.COUNT; i++) {
            Double var = Math.random();
            TextMessage textMessage = null;
            try {
                textMessage = session.createTextMessage(var.toString());
                messageProducer.send(textMessage);
                session.commit();//只能发一次提交一次数据，只有这样画图的才不会因为两个时间戳一样而产生错误
                Thread.sleep(10);
            }catch (JMSException | InterruptedException ee){
              ee.printStackTrace();
            }
        }

    }
}

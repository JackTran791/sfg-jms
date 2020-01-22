package com.luv2code.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.sfgjms.config.JmsConfig;
import com.luv2code.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

/**
 * @author Jack Tran
 */
@Component
@RequiredArgsConstructor
public class HelloSender {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World!!!")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World!!!")
                .build();

        Message receiveMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;

                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type","com.luv2code.sfgjms.model.HelloWorldMessage");

                    System.out.println("Sending Hello");

                    return helloMessage;
                } catch (JsonProcessingException e) {
                    throw new JMSException("Boom");
                }
            }
        });

        System.out.println(receiveMsg.getBody(String.class));
    }
}

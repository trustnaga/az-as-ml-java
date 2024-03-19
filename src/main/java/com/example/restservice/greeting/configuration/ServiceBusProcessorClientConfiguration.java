package com.example.restservice.greeting.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.identity.DefaultAzureCredential;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.spring.cloud.service.servicebus.consumer.ServiceBusErrorHandler;
import com.azure.spring.cloud.service.servicebus.consumer.ServiceBusRecordMessageListener;
import com.example.restservice.greeting.FontDoorService;

@Configuration(proxyBeanMethods = false)
public class ServiceBusProcessorClientConfiguration {
    
    @Autowired
    private FontDoorService fontDoorService;
    
    @Bean
    ServiceBusRecordMessageListener processMessage() {
        return context -> {
            ServiceBusReceivedMessage message = context.getMessage();
            System.out.printf("Processing message. Id: %s, Sequence #: %s. Contents: %s%n", message.getMessageId(),
                    message.getSequenceNumber(), message.getBody());
                    try {
                        fontDoorService.getFrontDoorFrontendEndpoint();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
        };
    }

     @Bean
    ServiceBusErrorHandler processError() {
        return context -> {
            System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
                    context.getFullyQualifiedNamespace(), context.getEntityPath());
        };
    }

   
}

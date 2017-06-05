package com.example.demo;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.support.MessageBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;


@SpringBootApplication
@EnableBinding(Processor.class)
public class EmergencyProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmergencyProcessorApplication.class, args);
    }

    private RuntimeService runtimeService;
    @Autowired
    private Source source;


    @Autowired
    public EmergencyProcessorApplication(final RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
        this.runtimeService.addEventListener(new ActivitiEventListener() {
            @Override
            public void onEvent(ActivitiEvent activitiEvent) {
                source.output().send(MessageBuilder.withPayload(activitiEvent.toString()).build());
            }

            @Override
            public boolean isFailOnException() {
                return false;
            }
        });
    }




    @Transformer(inputChannel = Processor.INPUT,
            outputChannel = Processor.OUTPUT)
    public Object transform(String emergency) {
        System.out.println("Emergency in the processor: " + emergency);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("waiter", Collections.singletonMap("customerId", (Object) 243L));
        System.out.println("Process started: "+processInstance.getId());

        return emergency;
    }
}
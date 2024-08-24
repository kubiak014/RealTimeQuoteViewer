package com.assignement.realtimequoteviewer;

import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.provider.MarketDataProviderRunnable;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
@EnableScheduling
public class RealTimeQuoteViewerApplication {

    MarketDataProviderRunnable marketDataProviderRunnable;

    @Autowired
    private SecurityRepository securityRepository;


    public static void main(String[] args) {
        SpringApplication.run(RealTimeQuoteViewerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");
            BlockingQueue<PriceUpdateEvent> priceUpdateChannel = new LinkedBlockingQueue<>();

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

            marketDataProviderRunnable = new MarketDataProviderRunnable(priceUpdateChannel);
            Thread thread = new Thread(marketDataProviderRunnable);
            thread.start();

            List<Security> securities = securityRepository.findAll();
            securities.forEach(security -> System.out.println("Security loaded from DB : " + security));

            QuoteViewer quoteViewer = new QuoteViewer(args[0], priceUpdateChannel);
            quoteViewer.start();
        };
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        System.out.println("Running");
    }

}

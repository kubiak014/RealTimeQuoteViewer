package com.assignement.realtimequoteviewer;

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

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

            marketDataProviderRunnable = new MarketDataProviderRunnable();
            Thread thread = new Thread(marketDataProviderRunnable);
            thread.start();

            Security aapl = securityRepository.findByTickerId("AAPL");
            System.out.println("Security loaded from DB : " + aapl);

            QuoteViewer quoteViewer = new QuoteViewer(args[0]);
            quoteViewer.start();
        };
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        System.out.println("Running");
    }

}

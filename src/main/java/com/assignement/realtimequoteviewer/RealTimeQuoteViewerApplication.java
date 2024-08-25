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

    @Autowired
    private SecurityRepository securityRepository;

    public static void main(String[] args) {
        SpringApplication.run(RealTimeQuoteViewerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            BlockingQueue<PriceUpdateEvent> priceUpdateChannel = new LinkedBlockingQueue<>();
            startMarketDataProviders(priceUpdateChannel, args);

            List<Security> securities = securityRepository.findAll();
            securities.forEach(security -> System.out.println("Security loaded from DB : " + security));

            QuoteViewer quoteViewer = new QuoteViewer(args[0], priceUpdateChannel, securityRepository);
            quoteViewer.start();
        };
    }

    private void startMarketDataProviders(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, String[] args) {
        System.out.println("Starting MarketData Producer(s)....");
        for(int i = 0; i < Integer.parseInt(args[1]); i++) {
            MarketDataProviderRunnable marketDataProviderRunnable = new MarketDataProviderRunnable(priceUpdateChannel, securityRepository, "MarketDataProducer" + i);
            Thread thread = new Thread(marketDataProviderRunnable );
            thread.start();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {}

}

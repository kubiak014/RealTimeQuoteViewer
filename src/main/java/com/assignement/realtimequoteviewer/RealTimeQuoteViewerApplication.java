package com.assignement.realtimequoteviewer;

import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.provider.MarketDataProviderRunnable;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import com.assignement.realtimequoteviewer.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
@EnableScheduling
public class RealTimeQuoteViewerApplication {

    @Autowired
    private SecurityRepository securityRepository;

    private final Logger logger = LoggerFactory.getLogger(RealTimeQuoteViewerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RealTimeQuoteViewerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            printExistingSecurities();
            BlockingQueue<PriceUpdateEvent> priceUpdateChannel = new LinkedBlockingQueue<>();
            int providerCount = getProviderCount(args);

            startMarketDataProviders(priceUpdateChannel, providerCount);
            SecurityService securityService = new SecurityService(this.securityRepository);

            QuoteViewer quoteViewer = new QuoteViewer(args[0], priceUpdateChannel, securityService);
            quoteViewer.start();
        };
    }

    private static int getProviderCount(String[] args) {
        int providerCount;
        if (args.length >= 2) {
            providerCount = Integer.parseInt(args[1]);
        } else {
            providerCount = 1;
        }
        return providerCount;
    }

    private void printExistingSecurities() {
        List<Security> securities = securityRepository.findAll();
        securities.forEach(security -> logger.info("Security loaded from DB : " + security));
    }

    private void startMarketDataProviders(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, int marketDataProviderCount) {
        this.logger.info("Starting MarketData Producer(s)....");
        for (int i = 0; i < marketDataProviderCount; i++) {
            MarketDataProviderRunnable marketDataProviderRunnable = new MarketDataProviderRunnable(priceUpdateChannel, securityRepository, "MarketDataProducer" + i);
            Thread thread = new Thread(marketDataProviderRunnable);
            thread.start();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
    }

}

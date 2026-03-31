package com.fpm.service.impl;

import com.fpm.model.CurrencyRate;
import com.fpm.model.CurrencyRateOverride;
import com.fpm.repository.CurrencyRateRepository;
import com.fpm.service.CurrencyRateSyncService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CurrencyRateSyncServiceImpl implements CurrencyRateSyncService {

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${fpm.currency.override.alert.to}")
    private String alertEmailTo;

    @Value("${fpm.currency.override.alert.from}")
    private String alertEmailFrom;

    // STORY: FPMAPP-9048 - Periodic synchronization job every hour
    @Scheduled(cron = "0 0 * * * *")
    public void scheduledSync() {
        try {
            syncRatesFromProvider();
        } catch (Exception e) {
            // TODO: Log error properly
            e.printStackTrace();
        }
    }

    // STORY: FPMAPP-9048 - Sync currency rates from third-party provider
    @Override
    public void syncRatesFromProvider() throws Exception {
        // TODO: Integrate with actual third-party currency exchange rate provider API
        // For demonstration, simulate fetching rates for some currency pairs

        Map<String, BigDecimal> fetchedRates = Map.of(
                "USD/EUR", new BigDecimal("0.85"),
                "USD/GBP", new BigDecimal("0.75"),
                "USD/JPY", new BigDecimal("110.00")
        );

        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, BigDecimal> entry : fetchedRates.entrySet()) {
            CurrencyRate rate = new CurrencyRate();
            rate.setCurrencyPair(entry.getKey());
            rate.setRate(entry.getValue());
            rate.setRateTimestamp(now);
            rate.setOverride(false);
            currencyRateRepository.save(rate);
        }
    }

    // STORY: FPMAPP-9048 - Send email alert for admin override
    @Override
    public void sendOverrideAlertEmail(CurrencyRateOverride override) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(alertEmailTo);
        helper.setFrom(alertEmailFrom);
        helper.setSubject("Currency Rate Override Alert: " + override.getCurrencyPair());

        StringBuilder sb = new StringBuilder();
        sb.append("Currency rate override performed by admin user: ").append(override.getAdminUserId()).append("\n");
        sb.append("Currency Pair: ").append(override.getCurrencyPair()).append("\n");
        sb.append("New Rate: ").append(override.getRate()).append("\n");
        sb.append("Effective Date: ").append(override.getEffectiveDate()).append("\n");
        sb.append("Override Timestamp: ").append(override.getOverrideTimestamp()).append("\n");

        helper.setText(sb.toString());

        mailSender.send(message);
    }

}

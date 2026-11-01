package tech.codehunt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import tech.codehunt.model.SiteSettings;
import tech.codehunt.repository.SiteSettingsRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private SiteSettingsRepository siteSettingsRepository;

    @Autowired
    public void setSiteSettingsRepository(SiteSettingsRepository siteSettingsRepository) {
        this.siteSettingsRepository = siteSettingsRepository;
    }

    @Override
    public void run(String... args) {
        // Runs on every startup, but only inserts when the table is empty
        if (siteSettingsRepository.count() == 0) {
            SiteSettings settings = new SiteSettings();
            settings.setContactNumber("+1800 559 6580");
            settings.setEmail("info@taxi.com");
            settings.setMapIframeUrl("https://www.google.com/maps?q=New+York&output=embed");
            siteSettingsRepository.save(settings);
            System.out.println("Default SiteSettings record created");
        }
    }
}

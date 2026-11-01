package tech.codehunt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.codehunt.model.SiteSettings;

@Repository
public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Integer> {

}

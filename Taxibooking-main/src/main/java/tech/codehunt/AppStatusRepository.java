package tech.codehunt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.codehunt.model.AppStatus;

@Repository
public interface AppStatusRepository extends JpaRepository<AppStatus, Integer> {

}

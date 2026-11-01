package tech.codehunt.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.codehunt.model.ServiceForm;

@Repository
public interface ServiceFormCrud extends JpaRepository<ServiceForm, Integer>{
     
}

package lt.fxportal.repositories;

import lt.fxportal.domain.entities.Currency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICurrencyRepository extends JpaRepository<Currency, String>{
    
}
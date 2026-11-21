package lt.fxportal.infrastructure.repository;

import lt.fxportal.domain.entity.Currency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICurrencyRepository extends JpaRepository<Currency, String>{
    
}
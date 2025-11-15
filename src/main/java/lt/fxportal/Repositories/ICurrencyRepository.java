package lt.fxportal.Repositories;

import lt.fxportal.Domain.Entities.Currency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICurrencyRepository extends JpaRepository<Currency, String>{
    
}
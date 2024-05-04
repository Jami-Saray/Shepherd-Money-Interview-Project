package com.shepherdmoney.interviewproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shepherdmoney.interviewproject.model.CreditCard;


import java.util.List;
/**
 * Crud repository to store credit cards
 */
@Repository("CreditCardRepo")
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {
    
    public CreditCard findByCardNumber(String cardNumber);
    public List<CreditCard> findByOwner(int userId);

    
}

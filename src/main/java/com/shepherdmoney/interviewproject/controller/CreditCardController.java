package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;

/**
 *
 * @author saff
 */
@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;
    /**
     *
     * @param payload
     * @return
     */
    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // TODO: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length
        try {
            User user= userRepository.findById(payload.getUserId());
            if (user==null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
    
            CreditCard creditCard = new CreditCard();
            creditCard.setNumber(payload.getCardNumber());
            creditCard.setIssuanceBank(payload.getCardIssuanceBank());
            creditCard.setOwner(user);  // 设置信用卡的所有者
            creditCardRepository.save(creditCard);
    
            // 返回成功响应，包含信用卡ID
            return ResponseEntity.ok(creditCard.getId());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // TODO: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null
        // return null;
        try {
            User  user= userRepository.findById(userId);
            if (user==null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
    
            List<CreditCard> creditCards = creditCardRepository.findByOwner(userId); // 获取用户的所有信用卡
            List<CreditCardView> creditCardViews = creditCards.stream()
            .map(card -> new CreditCardView(card.getNumber(), card.getIssuanceBank()))
            .collect(Collectors.toList());
    
            return ResponseEntity.ok(creditCardViews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // TODO: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request
        // return null;
        try {
            CreditCard card = creditCardRepository.findByCardNumber(creditCardNumber);
            if (card == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.ok(card.getOwner().getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        //TODO: Given a list of transactions, update credit cards' balance history.
        //      1. For the balance history in the credit card
        //      2. If there are gaps between two balance dates, fill the empty date with the balance of the previous date
        //      3. Given the payload `payload`, calculate the balance different between the payload and the actual balance stored in the database
        //      4. If the different is not 0, update all the following budget with the difference
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a balance amount of {date: 4/11, amount: 110}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 100}]
        //      This is because
        //      1. You would first populate 4/11 with previous day's balance (4/10), so {date: 4/11, amount: 100}
        //      2. And then you observe there is a +10 difference
        //      3. You propagate that +10 difference until today
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.
        CreditCard card = creditCardRepository.findByCardNumber(payload[0].getCardNumber());
        if (card == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        for (UpdateBalancePayload updateBalancePayload : payload) {
            if (!updateBalancePayload.getCardNumber().equals(card.getNumber())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            card.updateBalance(updateBalancePayload.getBalanceDate(), (int) updateBalancePayload.getBalanceAmount());
        }
        


        return null;
    }
    
}

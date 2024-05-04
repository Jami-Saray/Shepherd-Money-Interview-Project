package com.shepherdmoney.interviewproject.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String issuanceBank;

    private String number;

    private String cardNumber;

//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

     @OneToMany
    private ArrayList<BalanceHistory> balanceHistory;




    public void updateBalance(LocalDate updateDate, int newBalance) {
        // 首先确保余额记录按日期排序
        balanceHistory.sort(Comparator.comparing(BalanceHistory::getDate));
    
        // 找到更新日期或之前最近的记录
        BalanceHistory lastRecordBeforeUpdate = null;
        for (BalanceHistory record : balanceHistory) {
            if (!record.getDate().isAfter(updateDate)) {
                lastRecordBeforeUpdate = record;
            } else {
                break;
            }
        }
    
        // 如果给定日期之前没有记录，则从前一天开始填充
        int startingBalance = (lastRecordBeforeUpdate != null) ? (int)lastRecordBeforeUpdate.getBalance() : 0;
        LocalDate startDate = (lastRecordBeforeUpdate != null) ? lastRecordBeforeUpdate.getDate().plusDays(1) : updateDate;
    
        // 填充从最后记录日期到更新日期的记录
        for (LocalDate date = startDate; date.isBefore(updateDate); date = date.plusDays(1)) {
            BalanceHistory newone = new BalanceHistory();
            newone.setBalance(startingBalance);
            newone.setDate(date);
            balanceHistory.add(newone);
        }
    
        // 现在处理更新日期的记录
        BalanceHistory updateDayRecord = balanceHistory.stream()
            .filter(b -> b.getDate().equals(updateDate))
            .findFirst()
            .orElse(null);
    
        if (updateDayRecord == null) {
            BalanceHistory newRecord = new BalanceHistory();
            newRecord.setDate(updateDate);
            newRecord.setBalance(newBalance);
            balanceHistory.add(newRecord);
            updateDayRecord = newRecord;
        } else {
            updateDayRecord.setBalance(newBalance);
        }
    
        // 计算差值并更新该日期及之后所有日期的余额
        int difference = newBalance - startingBalance;
        boolean startUpdating = false;
        for (BalanceHistory record : balanceHistory) {
            if (record.getDate().isEqual(updateDate) || startUpdating) {
                record.setBalance(record.getBalance() + difference);
                startUpdating = true;
            }
        }
    
        // 保证余额历史记录按日期排序
        balanceHistory.sort(Comparator.comparing(BalanceHistory::getDate));
    }










    // TODO: Credit card's owner. For detailed hint, please see User class
    // Some field here <> owner;

    // TODO: Credit card's balance history. It is a requirement that the dates in the balanceHistory 
    //       list must be in chronological order, with the most recent date appearing first in the list. 
    //       Additionally, the last object in the "list" must have a date value that matches today's date, 
    //       since it represents the current balance of the credit card.
    //       This means that if today is 04-16, and the list begin as empty, you receive a payload for 04-13,
    //       you should fill the list up until 04-16. For example:
    //       [
    //         {date: '2023-04-10', balance: 800},
    //         {date: '2023-04-11', balance: 1000},
    //         {date: '2023-04-12', balance: 1200},
    //         {date: '2023-04-13', balance: 1100},
    //         {date: '2023-04-16', balance: 900},
    //       ]
    // ADDITIONAL NOTE: For the balance history, you can use any data structure that you think is appropriate.
    //        It can be a list, array, map, pq, anything. However, there are some suggestions:
    //        1. Retrieval of a balance of a single day should be fast
    //        2. Traversal of the entire balance history should be fast
    //        3. Insertion of a new balance should be fast
    //        4. Deletion of a balance should be fast
    //        5. It is possible that there are gaps in between dates (note the 04-13 and 04-16)
    //        6. In the condition that there are gaps, retrieval of "closest **previous**" balance date should also be fast. Aka, given 4-15, return 4-13 entry tuple
}

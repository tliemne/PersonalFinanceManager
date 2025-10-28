package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.RecurringTransaction;
import com.example.PersonalFinanceManager.repository.RecurringTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecurringTransactionService implements RecurringTransactionServiceImpl {
   @Autowired
   private RecurringTransactionRepository recurringTransactionRepository;
    @Override
    public RecurringTransaction createRecurringTransaction(RecurringTransaction rt) {
        return recurringTransactionRepository.save(rt);
    }

    @Override
    public Optional<RecurringTransaction> getRecurringTransactionById(Long id) {
        return recurringTransactionRepository.findById(id);
    }

    @Override
    public List<RecurringTransaction> getAllRecurringTransactions() {
        return recurringTransactionRepository.findAll();
    }

    @Override
    public RecurringTransaction updateRecurringTransaction(Long id, RecurringTransaction rt) {
        return  recurringTransactionRepository.findById(id).map(e->{
            e.setAmount(rt.getAmount());
            e.setFrequency(rt.getFrequency());
            e.setNextDate(rt.getNextDate());
            e.setDescription(rt.getDescription());
            return recurringTransactionRepository.save(e);
        }).orElseThrow(() -> new RuntimeException("not found"));
    }

    @Override
    public void deleteRecurringTransaction(Long id) {
        if(!recurringTransactionRepository.existsById(id))
        {
            throw new RuntimeException("not found");
        }
        recurringTransactionRepository.deleteById(id);
    }

}

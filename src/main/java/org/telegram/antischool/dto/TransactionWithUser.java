package org.telegram.antischool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.telegram.antischool.model.Transaction;
import org.telegram.antischool.model.User;

@Data
@AllArgsConstructor
@ToString
public class TransactionWithUser {
    private Transaction transaction;
    private User user;
}

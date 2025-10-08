package org.telegram.antischool.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("transactions")
@Data
@RequiredArgsConstructor
@ToString
public class Transaction {
    @Id
    private Long id;
    @Column("user_id")
    @NonNull
    private Long userId;
    @CreatedDate
    private LocalDateTime createdAt;
    @NonNull
    private Type type;
    @NonNull
    private String description;
    @NonNull
    private int amount;
}

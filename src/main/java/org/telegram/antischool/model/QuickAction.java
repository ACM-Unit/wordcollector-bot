package org.telegram.antischool.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("quick_actions")
@Data
@RequiredArgsConstructor
public class QuickAction {
    @Id
    private long id;
    @Column("user_id")
    @NonNull
    private Long userId;
    @NonNull
    private String name;
    @NonNull
    private int amount;
    @NonNull
    private Type type;
}

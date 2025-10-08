package org.telegram.antischool.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Data
@ToString
public class User {
    @Id
    private long id;
    private String name;
    private int housePoints = 0;
}

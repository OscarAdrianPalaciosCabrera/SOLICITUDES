package com.crediya.r2dbc.data;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("loans_type")
public class LoanTypeData {
    @Id
    private String id;
    private String loanType;
    private Double interestRate;

}

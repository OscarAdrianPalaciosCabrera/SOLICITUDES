package com.crediya.model.loantype;
import com.crediya.model.loanrequest.LoanRequest;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {

    private String loanType;

}



package com.crediya.api.mapper;

import com.crediya.api.dto.CreateLoanRequestDTO;
import com.crediya.model.loanrequest.LoanRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface RequestDtoMapper {
    @ObjectFactory
    default LoanRequest toModel(CreateLoanRequestDTO dto){
        if(dto == null) return null;
        return  LoanRequest.create(
                dto.amount(),
                dto.identityDocumentApplicant(),
                dto.timeLimit(),
                dto.loanType(),
                dto.state()
        );
    }
    CreateLoanRequestDTO toResponse(LoanRequest loanRequest);
}

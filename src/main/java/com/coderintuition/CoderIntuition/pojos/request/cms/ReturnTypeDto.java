package com.coderintuition.CoderIntuition.pojos.request.cms;

import com.coderintuition.CoderIntuition.enums.ArgumentType;
import com.coderintuition.CoderIntuition.enums.UnderlyingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ReturnTypeDto {
    private ArgumentType type;

    private UnderlyingType underlyingType;

    private UnderlyingType underlyingType2;

    @NotNull
    private Boolean orderMatters;
}

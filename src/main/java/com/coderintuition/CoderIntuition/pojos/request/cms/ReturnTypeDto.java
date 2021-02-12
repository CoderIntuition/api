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
    @NotNull
    private ArgumentType type;

    @NotNull
    private UnderlyingType underlyingType;

    @NotNull
    private UnderlyingType underlyingType2;

    @NotNull
    private Boolean orderMatters;
}

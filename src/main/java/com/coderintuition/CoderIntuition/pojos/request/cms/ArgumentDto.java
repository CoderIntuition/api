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
public class ArgumentDto {
    @NotNull
    ArgumentType type;

    @NotNull
    UnderlyingType underlyingType;

    @NotNull
    UnderlyingType underlyingType2;
}

package com.coderintuition.CoderIntuition.pojos.request.cms;

import com.coderintuition.CoderIntuition.enums.ArgumentType;
import com.coderintuition.CoderIntuition.enums.UnderlyingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArgumentDto {
    ArgumentType type;
    UnderlyingType underlyingType;
    UnderlyingType underlyingType2;
}

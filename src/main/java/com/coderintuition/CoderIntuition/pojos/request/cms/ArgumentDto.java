package com.coderintuition.CoderIntuition.pojos.request.cms;

import com.coderintuition.CoderIntuition.models.ArgumentType;
import com.coderintuition.CoderIntuition.models.UnderlyingType;
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

package com.coderintuition.CoderIntuition.pojos.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnType {
    ArgumentType type;
    UnderlyingType underlyingType;
}

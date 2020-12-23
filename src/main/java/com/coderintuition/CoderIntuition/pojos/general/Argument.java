package com.coderintuition.CoderIntuition.pojos.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Argument {
    ArgumentType type;
    UnderlyingType underlyingType;
    String value;
}

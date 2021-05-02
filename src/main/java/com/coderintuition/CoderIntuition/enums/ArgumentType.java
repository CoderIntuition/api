package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ArgumentType {
    @JsonProperty("STRING") STRING,
    @JsonProperty("INTEGER") INTEGER,
    @JsonProperty("FLOAT") FLOAT,
    @JsonProperty("BOOLEAN") BOOLEAN,
    @JsonProperty("LIST") LIST,
    @JsonProperty("ARRAY_2D") ARRAY_2D,
    @JsonProperty("LIST_OF_LISTS") LIST_OF_LISTS,
    @JsonProperty("DICTIONARY") DICTIONARY,
    @JsonProperty("TREE") TREE,
    @JsonProperty("LINKED_LIST") LINKED_LIST,
    @JsonProperty("LINKED_LIST_WITH_CYCLE") LINKED_LIST_WITH_CYCLE,
}

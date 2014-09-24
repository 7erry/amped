package com.amped.helloworld.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import java.io.Serializable;

import com.wordnik.swagger.annotations.*;

@ApiModel(value = "A saying used to greet a person")
public class Saying implements Serializable {
    public long id;

    @Length(max = 3)
    public String content;

    public Saying() {
        // Jackson deserialization
    }

    public Saying(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @ApiModelProperty(value = "Saying ID", required=true)
    @JsonProperty
    public long getId() {
        return id;
    }

    @ApiModelProperty(value = "Content", required=true)
    @JsonProperty
    public String getContent() {
        return content;
    }
}

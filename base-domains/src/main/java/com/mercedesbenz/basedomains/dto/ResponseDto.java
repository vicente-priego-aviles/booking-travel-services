package com.mercedesbenz.basedomains.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorDto error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
}

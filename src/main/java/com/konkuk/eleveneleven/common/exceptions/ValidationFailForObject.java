package com.konkuk.eleveneleven.common.exceptions;

import lombok.*;
import org.springframework.validation.ObjectError;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationFailForObject {

    private String objectName;
    private String defaultMessage;

    public ValidationFailForObject(ObjectError error){
        this.defaultMessage = error.getDefaultMessage();
        this.objectName = error.getObjectName();
    }
}

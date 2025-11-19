package lt.fxportal.API.DTOs;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JacksonXmlRootElement(localName = "Error")
public class ErrorResponseDTO {
    private String message;
    private int code;
}


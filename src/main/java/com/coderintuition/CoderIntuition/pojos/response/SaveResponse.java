package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.models.Save;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveResponse {
    private String pythonCode;
    private String javaCode;
    private String javascriptCode;

    public static SaveResponse fromSave(Save save) {
        SaveResponse saveResponse = new SaveResponse();
        saveResponse.setPythonCode(save.getPythonCode());
        saveResponse.setJavaCode(save.getJavaCode());
        saveResponse.setJavascriptCode(save.getJavascriptCode());
        return saveResponse;
    }
}

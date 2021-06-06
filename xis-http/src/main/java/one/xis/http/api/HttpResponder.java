package one.xis.http.api;

import one.xis.Inject;
import one.xis.Singleton;
import one.xis.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Singleton
public class HttpResponder {

    @Inject
    private ObjectMapper objectMapper;

    public void sendResponse(Object entity, HttpServletResponse response) {
        if (entity != null) {
            response.setContentType(ContentType.JSON.getString());
            try {
                objectMapper.writeValue(response.getOutputStream(), entity);
            } catch (IOException e) {
                log.error("unable to send response", e);
            }
        }
    }
}

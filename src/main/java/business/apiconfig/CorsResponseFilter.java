package business.apiconfig;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;


@Provider
public class CorsResponseFilter implements ContainerResponseFilter {
    /**
     * The constant ALLOWED_METHODS.
     */
    public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    /**
     * The constant MAX_AGE.
     */
    public static final int MAX_AGE = 42 * 60 * 60;
    /**
     * The constant DEFAULT_ALLOWED_HEADERS.
     */
    public static final String DEFAULT_ALLOWED_HEADERS = "Content-Disposition,origin,accept,content-type,X-FORWARDED-FOR,X-REAL-IP,HTTP_X_REAL_IP,Location";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", getRequestedHeaders(responseContext));
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Expose-Headers", getRequestedHeaders(responseContext));
        headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
        headers.add("Access-Control-Max-Age", MAX_AGE);
        headers.add("x-responded-by", "core-response-filter");

    }

    /**
     * Gets requested headers.
     *
     * @param responseContext the response context
     * @return the requested headers
     */
    String getRequestedHeaders(ContainerResponseContext responseContext) {
        List<Object> headers = responseContext.getHeaders().get("Access-Control-Request-Headers");
        return createHeaderList(headers);
    }

    /**
     * Create header list.
     *
     * @param headers the headers
     * @return the string
     */
    String createHeaderList(List<Object> headers) {
        if (headers == null || headers.isEmpty()) {
            return DEFAULT_ALLOWED_HEADERS;
        }
        StringBuilder retVal = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            String header = (String) headers.get(i);
            retVal.append(header);
            if (i != headers.size() - 1) {
                retVal.append(',');
            }
        }
        return retVal.toString();
    }
}

package org.example.gun5.RateLimit.src.main.java.com.example.RateLimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TransferRateLimitFilter extends OncePerRequestFilter {

    private static final String TARGET_ENDPOINT = "api/transfer";
    private static final String TARGET_METHOD = "POST";
    private static final String HEADER_CONSUMER_ID = "X-Costumer_Id";

    private final RateLimitService rateLimitService;

    public TransferRateLimitFilter(final RateLimitService rateLimitService) {this.rateLimitService = rateLimitService;}

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        if(!isTargetRequest(request)){
            filterChain.doFilter(request,response);
            return;

        }

        String Customer_Id = request.getHeader(HEADER_CONSUMER_ID);
        if(Customer_Id == null || Customer_Id.isBlank()){
            sendErrorResponse(
                    response,
                    HttpStatus.BAD_REQUEST.value(),
                    "Missing required header: " + HEADER_CONSUMER_ID
            );
            return;
        }


        boolean allowed = rateLimitService.allowRequest(Customer_Id);

        if(!allowed){
            sendErrorResponse(
                    response,
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Rate limit exceeded. Maximum 5 transfer requests per minute allowed."
            );
            return;
        }

        filterChain.doFilter(request,response);



    }


    public boolean isTargetRequest(HttpServletRequest request){

        return  TARGET_ENDPOINT.equals(request.getRequestURI())&&
                TARGET_METHOD.equalsIgnoreCase(request.getMethod());

    }



    private void sendErrorResponse(
            HttpServletResponse response,
            int statusCode,
            String message
    ) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                statusCode,
                HttpStatus.valueOf(statusCode).getReasonPhrase(),
                message
        );

        response.getWriter().write(json);
    }
}

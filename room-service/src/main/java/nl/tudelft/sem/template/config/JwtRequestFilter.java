package nl.tudelft.sem.template.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.template.communication.ServiceCommunication;
import nl.tudelft.sem.template.entities.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private transient UserInfo userInfo = null;
    private transient String retVal = null;

    /**
     * Default constructor.
     */
    public JwtRequestFilter() {
    }

    /**
     * Checks if the request's jwt is valid by calling the authentication microservice.
     * Duplicate literals suppressed as identical strings were used multiple times for URLs.
     *
     * @param request Incoming HTTP Request
     * @param response Outgoing HTTP Responce
     * @param filterChain The filterchain to foward to the next filter.
     * @throws ServletException Filter exception.
     * @throws IOException Filter exception.
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String authenticateUri = "http://localhost:8081";

        try {
            retVal = ((String) ServiceCommunication.sendGetRequest((authenticateUri + "/validate"),
                authorizationHeader,
                "Conflict in authentication.",
                String.class));
            if (retVal == null || !retVal.equals("true")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid JWT");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }

        if (retVal != null) {
            String url = (authenticateUri + "/getUserInfo");
            userInfo = ((UserInfo) ServiceCommunication.sendGetRequest(url,
                authorizationHeader,
                "Conflict in authentication.",
                UserInfo.class));


        }

        if (userInfo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateRequest(request, userInfo);
        }

        filterChain.doFilter(request, response);

    }

    protected void authenticateRequest(HttpServletRequest request, UserInfo userInfo) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userInfo.getRoles()));

        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(userInfo, null, authorities);

        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(token);

    }

}

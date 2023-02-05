package nl.tudelft.sem.template.security;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.template.entities.AuthenticationRequest;
import nl.tudelft.sem.template.entities.AuthenticationResponse;
import nl.tudelft.sem.template.entities.UserInfo;
import nl.tudelft.sem.template.entities.UserPrincipal;
import nl.tudelft.sem.template.repositories.UserRepository;
import nl.tudelft.sem.template.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthenticationResource {

    @Autowired
    private transient AuthenticationManager authenticationManager;

    @Autowired
    private transient UserService userDetailsService;

    @Autowired
    private transient JwtUtil jwtTokenUtil;

    @Autowired
    private transient UserRepository userRepository;

    /**
     * Creates an authentication token.
     *
     * @param authenticationRequest The request for authentication
     * @return the JWT token
     * @throws Exception Exception if the user is not authenticated
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?>
        createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
        throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername((authenticationRequest.getUsername()));

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    /**
     * Validates incoming requests.
     *
     * @param request current httpRequest.
     * @param response current httpResponse.
     * @return True if valid jwt, else false.
     */
    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    @ResponseBody
    public Boolean validateUser(HttpServletRequest request, HttpServletResponse response) {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT missing in the header");
        }

        String jwt = authorizationHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(jwt);

        if (jwtTokenUtil.validateToken(jwt, username)) {
            try {
                //check if username exists.
                userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT user doesnt exist");
            }

            return true;
        }
        return false;
    }


    /**
     * Returns the role of the requested netID.
     *
     * @param netID the netID of the person whose role is requested
     * @return the role the person has
     */
    @RequestMapping(value = "/getRole/{netID}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getRoleByID(@PathVariable String netID) {
        if (userRepository.existsById(netID)) {
            return userRepository.findRoleByUsername(netID);
        } else {
            return "Doesn't Exist";
        }
    }

    /**
     * Returns a UserInfo object which has the user's name and roles based on passed jwt.
     *
     * @param request current httpRequest.
     * @param response current httpResponse.
     * @return The UserInfo with the user's name and role.
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public UserInfo getUserInfo(HttpServletRequest request,
                                HttpServletResponse response) {
        if (!validateUser(request, response)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JWT");
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);

        UserPrincipal user = (UserPrincipal) userDetailsService.loadUserByUsername(
                                                    jwtTokenUtil.extractUsername(jwt));

        return new UserInfo(user.getUsername(), user.getRoles());
    }
}


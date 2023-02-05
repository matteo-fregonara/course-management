package nl.tudelft.sem.template.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UserPrincipal implements UserDetails {
    private static final long serialVersionUID = 467934642678146547L;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserPrincipal(User user) {
        this.user = user;
    }


    /**
     * Returns a list of authorities.
     *
     * @return lsit of authories/roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoles()));
        //No roles for the user has been added yet.
        return authorities;
    }

    public String getRoles() {
        return user.getRoles();
    }

    @Override
    public String getPassword() {
        if (user == null) {
            return null;
        }
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getNetID();
    }

    //The below functions needs to be changed later depending on our security preferences.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

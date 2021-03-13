package fileupload.web.security;

import fileupload.web.user.UserAccount;
import fileupload.web.user.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class FileUploadUserDetailsService implements UserDetailsService {

    private UserAccountRepository userAccountRepository;

    public FileUploadUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserAccount user = userAccountRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + "のUserが見つかりませんでした。");
        }

        return User.withUsername(user.getEmail()).password(user.getPassword()).roles(user.getRole()).build();
    }
}

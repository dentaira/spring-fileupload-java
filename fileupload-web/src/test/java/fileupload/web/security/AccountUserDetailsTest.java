package fileupload.web.security;

import fileupload.web.user.UserAccount;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

@ExtendWith(SoftAssertionsExtension.class)
class AccountUserDetailsTest {

    @Test
    void testFindWolverine(SoftAssertions softly) {
        // given
        UserAccount account = new UserAccount(
                UUID.randomUUID(),
                "wolverine@marvel",
                "Wolverine",
                "password01",
                "USER");
        // when
        AccountUserDetails actual = new AccountUserDetails(account);
        // then
        softly.assertThat(actual.getUsername()).isEqualTo("wolverine@marvel");
        softly.assertThat(actual.getPassword()).isEqualTo("password01");
        softly.assertThat(actual).extracting(UserDetails::getAuthorities).asList()
                .containsExactly(new SimpleGrantedAuthority("ROLE_" + account.getRole()));
        softly.assertThat(actual.isAccountNonExpired()).isEqualTo(true);
        softly.assertThat(actual.isAccountNonLocked()).isEqualTo(true);
        softly.assertThat(actual.isCredentialsNonExpired()).isEqualTo(true);
        softly.assertThat(actual.isEnabled()).isEqualTo(true);
        softly.assertThat(actual).extracting(AccountUserDetails::getAccount).isEqualToComparingFieldByField(account);
    }

    @Test
    void testFindIronman(SoftAssertions softly) {
        // given
        UserAccount account = new UserAccount(
                UUID.randomUUID(),
                "ironman@marvel",
                "Ironman",
                "password02",
                "ADMIN");
        // when
        AccountUserDetails actual = new AccountUserDetails(account);
        // then
        softly.assertThat(actual.getUsername()).isEqualTo("ironman@marvel");
        softly.assertThat(actual.getPassword()).isEqualTo("password02");
        softly.assertThat(actual).extracting(UserDetails::getAuthorities).asList()
                .containsExactly(new SimpleGrantedAuthority("ROLE_" + account.getRole()));
        softly.assertThat(actual.isAccountNonExpired()).isEqualTo(true);
        softly.assertThat(actual.isAccountNonLocked()).isEqualTo(true);
        softly.assertThat(actual.isCredentialsNonExpired()).isEqualTo(true);
        softly.assertThat(actual.isEnabled()).isEqualTo(true);
        softly.assertThat(actual).extracting(AccountUserDetails::getAccount).isEqualToComparingFieldByField(account);
    }

}
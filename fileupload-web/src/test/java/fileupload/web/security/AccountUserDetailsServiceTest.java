package fileupload.web.security;

import fileupload.web.user.UserAccount;
import fileupload.web.user.UserAccountRepository;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(SoftAssertionsExtension.class)
@ExtendWith(MockitoExtension.class)
class AccountUserDetailsServiceTest {

    @InjectMocks
    AccountUserDetailsService sut;

    @Mock
    UserAccountRepository userAccountRepository;

    @Test
    void testFindWolverine(SoftAssertions softly) {
        // given
        UserAccount account = new UserAccount(
                UUID.randomUUID(),
                "wolverine@marvel",
                "Wolverine",
                "password01",
                "USER");
        when(userAccountRepository.findByEmail("wolverine@marvel")).thenReturn(account);
        // when
        UserDetails actual = sut.loadUserByUsername("wolverine@marvel");
        // then
        softly.assertThat(actual).isInstanceOf(AccountUserDetails.class);
        softly.assertThat(actual).extracting("account").isEqualToComparingFieldByField(account);
        softly.assertThat(actual).extracting(UserDetails::getAuthorities).asList()
                .containsExactly(new SimpleGrantedAuthority("ROLE_" + account.getRole()));
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
        when(userAccountRepository.findByEmail("ironman@marvel")).thenReturn(account);
        // when
        UserDetails actual = sut.loadUserByUsername("ironman@marvel");
        // then
        softly.assertThat(actual).isInstanceOf(AccountUserDetails.class);
        softly.assertThat(actual).extracting("account").isEqualToComparingFieldByField(account);
        softly.assertThat(actual).extracting(UserDetails::getAuthorities).asList()
                .containsExactly(new SimpleGrantedAuthority("ROLE_" + account.getRole()));
    }

    @Test
    void testAccountNotFound() {
        // given
        when(userAccountRepository.findByEmail("ironman@marvel")).thenReturn(null);
        // when and then
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> sut.loadUserByUsername("ironman@marvel"))
                .withMessage("ironman@marvelのUserが見つかりませんでした。");
    }
}
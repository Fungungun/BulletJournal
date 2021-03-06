package com.bulletjournal.clients;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bulletjournal.redis.RedisUserRepository;
import java.util.Optional;

import com.bulletjournal.repository.UserDaoJpa;
import org.junit.Assert;
import org.junit.Test;
import com.bulletjournal.config.SSOConfig;
import com.bulletjournal.controller.models.User;

/**
 * Tests {@link UserClient}
 */
public class UserClientTests {

    @Test
    public void testGetUser() throws Exception {

        String username = "BulletJournal";
        String expectedThumbnail = "https://1o24bbs.com/user_avatar/1o24bbs.com/bulletjournal/37/15287_2.png";
        String expectedAvatar = "https://1o24bbs.com/user_avatar/1o24bbs.com/bulletjournal/75/15287_2.png";

        RedisUserRepository redisUserRepository = mock(RedisUserRepository.class);
        when(redisUserRepository.findById(username)).thenReturn(Optional.empty());
        UserDaoJpa userDaoJpa = mock(UserDaoJpa.class);

        UserClient userClient = new UserClient(new SSOConfig(
                "https://1o24bbs.com"), redisUserRepository, userDaoJpa);

        User user = userClient.getUser(username);
        Assert.assertEquals(username, user.getName());
        Assert.assertEquals(expectedAvatar,
                user.getAvatar());
        Assert.assertEquals(expectedThumbnail,
                user.getThumbnail());
        Assert.assertEquals(6475, user.getId().intValue());
    }
}

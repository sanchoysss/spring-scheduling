package com.almi.springscheduling.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserScheduler {

    private UserDao userDao;

    public UserScheduler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void createUser() throws InterruptedException {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .age((byte) 25)
                .build();
        TimeUnit.SECONDS.sleep(6);
        userDao.createUser(user);
    }

    @Scheduled(fixedRate = 6, timeUnit = TimeUnit.SECONDS)
    @Async
    public void deleteOldUsers() throws InterruptedException {
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(5);
        userDao.deleteOldUsers(dateTime);
        Thread.sleep(7_000);
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS, initialDelay = 3)
    public void printInfo() throws InterruptedException {
        log.info("Starting method printInfo");
        Thread.sleep(7_000);
        log.info("Ending method printInfo");
    }
}

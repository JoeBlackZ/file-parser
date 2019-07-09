package com.joe.fileParser.repository;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.joe.fileParser.model.User;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Resource
    private UserRepository userRepository;


    @Test
    public void insert() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            list.add(new User("admin" + i, "管理员" + i, DigestUtil.md5Hex("password"), System.currentTimeMillis()));
        }
        List<User> result = this.userRepository.insertAll(list);
        System.err.println(result.size());
    }

    @Test
    public void updateById() {
        User user = new User();
        user.setAccount("admin===");
        user.setName("adminUpdate");
        boolean b = this.userRepository.updateById("5d22ec470deda33948f37953", user);
        System.err.println(b);
    }

    @Test
    public void deleteByIds() {
        long l = this.userRepository.deleteByIds(new String[]{"5d22ec470deda33948f37952", "5d22ec470deda33948f37953"});
        System.err.println(l);
    }

    @Test
    public void deleteById() {
        long l = this.userRepository.deleteById("5d22ec470deda33948f37951");
        System.err.println(l);
    }

    @Test
    public void findAll() {
        List<User> all = this.userRepository.findAll();
        System.err.println(all.size());
    }

    @Test
    public void findAllByPage() {
        List<User> allByPage = this.userRepository.findAllByPage(3, 3);
        System.err.println(allByPage.size());
    }

    @Test
    public void find() {
        User user = new User();
        user.setAccount("admin");
        List<User> list = this.userRepository.find(user);
        System.err.println(list);
    }

    @Test
    public void findByPage() {
        User user = new User();
        user.setPageNum(1);
        user.setPageSize(10);
        List<User> page = this.userRepository.findByPage(user);
        System.err.println(page);
    }

    @Test
    public void findOne() {
        User user = new User();
        user.setAccount("admin");
        User one = this.userRepository.findOne(user);
        System.err.println(one);
    }

    @Test
    public void findById() {
        User byId = this.userRepository.findById("5d22ec470deda33948f37950");
        System.err.println(byId);
    }

    @Test
    public void count() {
        long count = this.userRepository.count();
        System.err.println(count);
    }

    @Test
    public void count1() {
        User user = new User();
        user.setName("管理员");
        long count = this.userRepository.count(user);
        System.err.println(count);
    }

}
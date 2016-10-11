package org.javajidi.admin.infrastructure.persistence.jdbc;

import org.javajidi.admin.domain.repository.RoleRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qiang.xie
 * @date 2016/10/11
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    RoleRepository roleRepository;

    @org.junit.Test
    public void contains(){
        roleRepository.contains("a");
    }

}
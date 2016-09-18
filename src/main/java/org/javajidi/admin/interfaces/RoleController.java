package org.javajidi.admin.interfaces;

import org.javajidi.admin.application.RoleService;
import org.javajidi.admin.domain.modle.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qiang.xie
 * @date 2016/9/18
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    protected RoleService roleService;

    @RequestMapping(method = RequestMethod.POST)
    public Role create(@RequestBody Role role){
        roleService.create(role);
        return role;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void modify(@RequestBody Role role){
        roleService.modify(role);
    }

    @RequestMapping(value = "/{id}/disable",method = RequestMethod.PUT)
    public void switchStatus(@PathVariable("id") String id,@RequestParam("disable") boolean disable){
        roleService.switchStatus(id,disable);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public void delete(@PathVariable("id")String id){
         roleService.delete(id);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Role get(@PathVariable("id")String id){
        return roleService.get(id);
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public List<Role> list(){
        return roleService.list();
    }


}

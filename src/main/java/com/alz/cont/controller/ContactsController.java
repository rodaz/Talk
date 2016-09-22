package com.alz.cont.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Pattern;

@RestController
public class ContactsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/hello/content")
    public List getData(
            @RequestParam(value = "nameFilter", required = true) String filter,
            @RequestParam(value = "limit", defaultValue = "200") int limit,
            @RequestParam(value = "offset", defaultValue = "0") long offset) {
        Pattern pattern = Pattern.compile(filter);
//        for (int i = 0; i < 100; i++) {
//            jdbcTemplate.update("INSERT INTO contacts(id, name) values(?,?)",new Random().nextInt(),"john");
//        }
        List contactsList = new ArrayList(limit);
        long a = System.nanoTime();
        find(pattern, limit, limit, offset, contactsList);
        long b = System.nanoTime();
        System.out.println(b-a);
        return contactsList;
    }

    private void find(Pattern pattern, int limit, int amount, long offset, List contactsList){
        List<Map<String, Object>> list =  jdbcTemplate.queryForList("SELECT * FROM contacts LIMIT ? OFFSET ?", amount, offset);
        int cnt=0;
        for (Map elem : list) {
            if (!pattern.matcher((String)elem.get("name")).matches()){
                contactsList.add(elem);
            } else {
                cnt++;
            }
            if (contactsList.size()==limit){
                return;
            }
        }
//        find(pattern, limit, amount, offset+limit, contactsList);
        find(pattern, limit, cnt==amount&&amount<200?amount*2:amount, offset+limit, contactsList);
    }
}

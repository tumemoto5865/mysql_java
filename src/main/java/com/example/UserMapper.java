package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper<List<User>> {
    public List<User> mapRow(ResultSet rs, int rowNum)
    		throws SQLException {
        List<User> list = new ArrayList<>();
        User tmp_user = new User();
        tmp_user.setId(rs.getInt("id"));
        tmp_user.setName(rs.getString("name"));
        list.add(tmp_user);
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            list.add(user);
        }
        return list;
    }
}

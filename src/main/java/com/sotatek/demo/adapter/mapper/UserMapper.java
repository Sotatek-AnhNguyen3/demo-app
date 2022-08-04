package com.sotatek.demo.adapter.mapper;

import com.sotatek.demo.domain.entitiy.User;
import com.sotatek.demo.domain.enums.MemberType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String password = rs.getString("email");
        Double salary = rs.getDouble("salary");
        String memberTypeString = rs.getString("member_type");
        MemberType memberType = StringUtils.isEmpty(memberTypeString) ? null : MemberType.valueOf(memberTypeString);

        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .salary(salary)
                .memberType(memberType)
                .build();
    }
}

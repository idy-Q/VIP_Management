package io.github.idyq.service.VIP;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.dao.table.impl.MemberDaoImpl;
import io.github.idyq.model.entity.Member;
import io.github.idyq.util.SqliteJdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao = new MemberDaoImpl();

    @Override
    public List<Member> listAll() throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return memberDao.findAll(conn);
        }
    }

    @Override
    public List<Member> searchByName(String nameKeyword) throws SQLException {
        List<Member> result = new ArrayList<>();
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            List<Member> all = memberDao.findAll(conn);
            for (Member m : all) {
                if (m.getName() != null && m.getName().contains(nameKeyword)) {
                    result.add(m);
                }
            }
        }
        return result;
    }
}

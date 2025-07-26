package io.github.idyq.service.VIP;

import io.github.idyq.model.entity.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberService {
    /** 查询所有会员 */
    List<Member> listAll() throws SQLException;
    /** 根据姓名模糊查询会员 */
    List<Member> searchByName(String nameKeyword) throws SQLException;
}

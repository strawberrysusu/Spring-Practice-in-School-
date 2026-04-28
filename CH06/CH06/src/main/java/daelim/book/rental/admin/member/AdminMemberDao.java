package daelim.book.rental.admin.member;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminMemberDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AdminMemberDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertAdminAccount(AdminMemberVo adminMemberVo) {
        System.out.println("[AdminMemberDao.insertAdminAccount]");

        List<String> args = new ArrayList<>();
        String sql = "INSERT INTO TB_ADMIN_ACCOUNT (";

        if ("system".equals(adminMemberVo.getId())) {
            sql += "approval, ";
            args.add("1");
        }

        sql += "id, ";
        args.add(adminMemberVo.getId());

        sql += "password, ";
        args.add(bCryptPasswordEncoder.encode(adminMemberVo.getPassword()));

        sql += "name, ";
        args.add(adminMemberVo.getName());

        sql += "gender, ";
        args.add(adminMemberVo.getGender());

        sql += "part, ";
        args.add(adminMemberVo.getPart());

        sql += "position, ";
        args.add(adminMemberVo.getPosition());

        sql += "email, ";
        args.add(adminMemberVo.getEmail());

        sql += "phone, ";
        args.add(adminMemberVo.getPhone());

        sql += "regDate, modDate) ";

        if ("system".equals(adminMemberVo.getId())) {
            sql += "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        } else {
            sql += "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        }

        int result = -1;
        try {
            result = jdbcTemplate.update(sql, args.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean exsitAdminAccount(String id) {
        System.out.println("[AdminMemberDao.exsitAdminAccount]");

        String sql = "SELECT COUNT(*) FROM TB_ADMIN_ACCOUNT WHERE id = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);

        return result > 0;
    }
}

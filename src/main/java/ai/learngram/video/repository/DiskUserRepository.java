package ai.learngram.video.repository;

import ai.learngram.video.model.User;
import ai.learngram.video.repository.api.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("diskUserRepository")
public class DiskUserRepository implements UserRepository {

    @Autowired
    NamedParameterJdbcTemplate namedJdbc;

    @Override
    public boolean exists(User user) {
        String query = "SELECT count(*) FROM users WHERE email=:email";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("email", user.getEmail());
        int count = namedJdbc.queryForObject(query, parameterSource, Integer.class);
        return count > 0;
    }

    @Override
    public boolean save(User user) {
        String query;
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("email", user.getEmail());
        parameterSource.addValue("name", user.getName());
        parameterSource.addValue("password", user.getPassword());
        parameterSource.addValue("active", user.isEnabled());
        if(exists(user)) {
            query = "UPDATE users SET name=:name, password=:password, active=:active WHERE email=:email";
        }
        else {
            query = "INSERT INTO users (email, name, password) VALUES (:email, :name, :password)";
        }
        namedJdbc.update(query, parameterSource);
        return true;
    }

    @Override
    public User getByEmail(String email) {
        String query = "SELECT * FROM users WHERE email=:email";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("email", email);
        return namedJdbc.query(query, parameterSource, rs -> {
            if(rs.next()) {
                User user = new User(rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"));
                user.setEnabled(rs.getBoolean("active"));
                return user;
            }
            return null;
        });
    }
}

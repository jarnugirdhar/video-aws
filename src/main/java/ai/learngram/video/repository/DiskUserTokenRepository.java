package ai.learngram.video.repository;

import ai.learngram.video.repository.api.UserTokenRepository;
import ai.learngram.video.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("diskUserTokenRepository")
public class DiskUserTokenRepository implements UserTokenRepository {

    @Autowired
    NamedParameterJdbcTemplate namedJdbc;

    @Override
    public String store(String id) {
        String token = TokenGenerator.generate();
        String query = "INSERT INTO user_token (email, token) VALUES (:email, :token)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("email", id);
        parameterSource.addValue("token", token);
        namedJdbc.update(query, parameterSource);
        return token;
    }

    @Override
    public String fetchByToken(String token) {
        String query = "SELECT * FROM user_token WHERE token=:token";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("token", token);
        return namedJdbc.query(query, parameterSource, rs -> {
            if(rs.next()) {
                return rs.getString("email");
            }
            return null;
        });
    }

    @Override
    public boolean invalidate(String token) {
        String query = "DELETE FROM user_token WHERE token=:token";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("token", token);
        int affectedRows = namedJdbc.update(query, parameterSource);
        return affectedRows > 0;
    }
}

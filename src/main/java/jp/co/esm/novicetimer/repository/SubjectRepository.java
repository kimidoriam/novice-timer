package jp.co.esm.novicetimer.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.esm.novicetimer.domain.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Repository
@Transactional
public class SubjectRepository {

    @Data
    @AllArgsConstructor
    static class SubjectEntity {
        private int id;
        private String Title;
        private int minutes;
        private String idobataUser;
        private int agendaId;
    }

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate; // インメモリのDB作成

    /**
     * @param agendaId 取得したいアジェンダのID
     * @return アジェンダに対応するSubjectのリスト
     *          引数に紐づくアジェンダが無い場合は空のリストを返す
     * @throws SQLException DBアクセスに失敗した場合に投げられる
     */
    public List<Subject> findSubjectsInAgenda(int agendaId) throws SQLException {
        String sql = "SELECT title, minutes, idobataUser FROM subjects WHERE agendaId = :id"; // SQL文
        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("id", agendaId); // SQL引数の設定

        List<Subject> result = jdbcTemplate.query(sql, param, new RowMapper<Subject>() {
            @Override
            public Subject mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Subject(rs.getString("title"), rs.getInt("minutes"), rs.getString("idobataUser"));
            }
        }); // SQL文, パラメータ, 戻り値の型(クラス)
        return result;
    }

    /**
     * @param agendaId サブジェクトを登録するアジェンダのID
     * @param subject 登録するサブジェクト
     * @return true:登録成功  false:登録失敗
     */
    public boolean insertSubject(int agendaId, Subject subject) {
        String sql = "INSERT INTO subjects (title, minutes, idobataUser, agendaId)"
            + "VALUES (:subjectTitle, :subjectMinutes, :subjectIdobataUser, :subjectAgendaId)";
        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("subjectTitle", subject.getTitle())
            .addValue("subjectMinutes", subject.getMinutes())
            .addValue("subjectIdobataUser", subject.getIdobataUser())
            .addValue("subjectAgendaId",agendaId);

        return jdbcTemplate.update(sql, param) == 1 ? true : false;
    }

    /**
     * @param agendaId サブジェクトを登録するアジェンダのID
     * @param subjects 登録するサブジェクトのリスト
     * @return true：登録成功  false：登録失敗
     */
    public boolean insertSubjectList(int agendaId, List<Subject> subjects) {
        if(subjects.size() < 1) { // リストに要素が無かった場合falseを返す
            return false;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO subjects (title, minutes, idobataUser, agendaId) VALUES");
        subjects.forEach(s -> {
            sql.append(" ('"
            + s.getTitle()
            + "', "
            + String.valueOf(s.getMinutes())
            + ", '"
            + s.getIdobataUser()
            + "', "
            + String.valueOf(agendaId)
            + "),");
        });
        sql.delete(sql.length() - 1, sql.length());
        SqlParameterSource param = new MapSqlParameterSource();
        return jdbcTemplate.update(sql.toString(),  param) != 0 ? true : false;
    }

    /**
     * 1つのアジェンダに紐づくサブジェクトを全て削除する
     * 削除に失敗した場合は、ロールバックされてメソッド呼び出し前と同じ状態になるハズ
     * @param id 削除するアジェンダのID
     * @return true:削除成功  false:削除失敗
     */
    public boolean deleteOneAgenda(int id) {
        String sql = "DELETE FROM subjects WHERE agendaId=:id";
        SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.update(sql, param) > 0 ? true : false;
    }

    /**
     * 全てのアジェンダとサブジェクトを削除する
     * 削除に失敗した場合は、ロールバックされてメソッド呼び出し前と同じ状態になるハズ
     * @return true:削除成功  false:削除失敗
     */
    public boolean deleteAllAgendas() {
        String sql = "DELETE FROM subjects";
        SqlParameterSource param = new MapSqlParameterSource();
        return jdbcTemplate.update(sql, param) > 0 ? true : false;
    }
}

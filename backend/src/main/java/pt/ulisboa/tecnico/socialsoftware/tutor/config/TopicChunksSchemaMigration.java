package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A piece of a document used to be under one topic at most (topic_chunks had a unique chunk_id) and
 * can now be under several, once each (unique topic_id and chunk_id). Hibernate's schema update adds
 * the new constraint but never drops the old one, so a database created before keeps refusing the
 * second topic of a piece until the old constraint is dropped here, once, when the server starts.
 */
@Component
public class TopicChunksSchemaMigration implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(TopicChunksSchemaMigration.class);

    private final JdbcTemplate jdbc;

    public TopicChunksSchemaMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            dropOldUniqueChunk();
        } catch (RuntimeException e) {
            // The server works without it, except for pieces under several topics
            logger.warn("Could not drop the old unique constraint of topic_chunks.chunk_id: {}", e.getMessage());
        }
    }

    /** Drops the unique constraints that cover chunk_id alone; returns how many there were (none once migrated). */
    public int dropOldUniqueChunk() {
        String product = jdbc.execute((ConnectionCallback<String>) connection -> connection.getMetaData().getDatabaseProductName());
        if (!"PostgreSQL".equals(product))
            return 0; // new databases (H2 in the tests) are created with the new constraint only

        List<String> names = jdbc.queryForList("""
                SELECT con.conname
                FROM pg_constraint con
                JOIN pg_class rel ON rel.oid = con.conrelid
                WHERE rel.relname = 'topic_chunks'
                  AND rel.relnamespace = to_regnamespace(current_schema())
                  AND con.contype = 'u'
                  AND array_length(con.conkey, 1) = 1
                  AND EXISTS (SELECT 1 FROM pg_attribute att
                              WHERE att.attrelid = rel.oid AND att.attnum = con.conkey[1] AND att.attname = 'chunk_id')
                """, String.class);
        for (String name : names) {
            jdbc.execute("ALTER TABLE topic_chunks DROP CONSTRAINT \"" + name.replace("\"", "\"\"") + "\"");
            logger.info("Dropped the old unique constraint {} of topic_chunks.chunk_id: a piece can be under several topics", name);
        }
        return names.size();
    }
}

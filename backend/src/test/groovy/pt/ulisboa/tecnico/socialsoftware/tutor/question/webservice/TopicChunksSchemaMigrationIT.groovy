package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TopicChunksSchemaMigration

/** A database made before pieces could be under several topics has a unique chunk_id that has to go. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TopicChunksSchemaMigrationIT extends SpockTestIT {
    @Autowired
    TopicChunksSchemaMigration migration
    @Autowired
    JdbcTemplate jdbc

    def uniqueOnChunkAlone() {
        return jdbc.queryForObject("""
                SELECT count(*) FROM pg_constraint con JOIN pg_class rel ON rel.oid = con.conrelid
                WHERE rel.relname = 'topic_chunks' AND con.contype = 'u' AND array_length(con.conkey, 1) = 1
                  AND EXISTS (SELECT 1 FROM pg_attribute att WHERE att.attrelid = rel.oid
                              AND att.attnum = con.conkey[1] AND att.attname = 'chunk_id')
                """, Integer.class)
    }

    def "the old unique chunk_id is dropped, once, and the new constraint stays"() {
        given: 'a database from before: topic_chunks with a unique chunk_id'
        migration.dropOldUniqueChunk()
        jdbc.execute('ALTER TABLE topic_chunks ADD CONSTRAINT uk_old_chunk_id UNIQUE (chunk_id)')

        expect:
        uniqueOnChunkAlone() == 1

        when:
        def dropped = migration.dropOldUniqueChunk()

        then:
        dropped == 1
        uniqueOnChunkAlone() == 0
        migration.dropOldUniqueChunk() == 0

        and: 'a topic still holds a piece once'
        jdbc.queryForObject("""
                SELECT count(*) FROM pg_constraint con JOIN pg_class rel ON rel.oid = con.conrelid
                WHERE rel.relname = 'topic_chunks' AND con.contype = 'u' AND array_length(con.conkey, 1) = 2
                """, Integer.class) == 1
    }
}

package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.ApiKeyCipher
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TEACHER_API_KEYS_NOT_CONFIGURED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TEACHER_API_KEY_UNREADABLE

class ApiKeyCipherTest extends Specification {
    static final String MASTER = Base64.encoder.encodeToString((0..31) as byte[])
    static final String OTHER_MASTER = Base64.encoder.encodeToString((1..32) as byte[])
    static final String KEY = 'nvapi-SECRET-key-0123456789'

    @TempDir
    Path tempDir

    def cipher = new ApiKeyCipher(MASTER)

    def "a key comes back as it was, and what is stored does not hold it"() {
        when:
        def stored = cipher.encrypt(7, 'nvidia_nim', KEY)

        then:
        cipher.decrypt(7, 'nvidia_nim', stored) == KEY
        !stored.contains(KEY)
        !new String(Base64.decoder.decode(stored), 'ISO-8859-1').contains('SECRET')
    }

    def "the same key is stored differently every time"() {
        expect:
        cipher.encrypt(7, 'nvidia_nim', KEY) != cipher.encrypt(7, 'nvidia_nim', KEY)
    }

    def "a changed row cannot be read"() {
        given:
        def bytes = Base64.decoder.decode(cipher.encrypt(7, 'nvidia_nim', KEY))
        bytes[bytes.length - 1] = (byte) (bytes[bytes.length - 1] ^ 1)

        when:
        cipher.decrypt(7, 'nvidia_nim', Base64.encoder.encodeToString(bytes))

        then:
        thrown(TutorException)
    }

    def "a row cannot be read as another teacher's or for another provider"() {
        given:
        def stored = cipher.encrypt(7, 'nvidia_nim', KEY)

        when:
        cipher.decrypt(8, 'nvidia_nim', stored)

        then:
        thrown(TutorException)

        when:
        cipher.decrypt(7, 'openai', stored)

        then:
        thrown(TutorException)
    }

    def "a row cannot be read with another master key"() {
        when:
        new ApiKeyCipher(OTHER_MASTER).decrypt(7, 'nvidia_nim', cipher.encrypt(7, 'nvidia_nim', KEY))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEY_UNREADABLE
    }

    def "garbage is not a key"() {
        when:
        cipher.decrypt(7, 'nvidia_nim', stored)

        then:
        thrown(TutorException)

        where:
        stored << ['', 'AAAA', 'not base64 at all!!']
    }

    def "with no master key nothing can be kept"() {
        given:
        def unconfigured = new ApiKeyCipher('')

        expect:
        !unconfigured.isConfigured()

        when:
        unconfigured.encrypt(7, 'nvidia_nim', KEY)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEYS_NOT_CONFIGURED
    }

    def "a master key that is not 32 bytes of base64 stops the server instead of weakening the cipher"() {
        when:
        new ApiKeyCipher(master)

        then:
        thrown(IllegalStateException)

        where:
        master << ['short', Base64.encoder.encodeToString(new byte[16]), '%%%not-base64%%%']
    }

    def "with no key given, the first start makes one in the file, for the owner only, and the next starts read it"() {
        given:
        def file = tempDir.resolve('secrets/generation-master.key')

        when:
        def first = new ApiKeyCipher('', file.toString())
        def stored = first.encrypt(7, 'nvidia_nim', KEY)

        then:
        first.isConfigured()
        Files.exists(file)
        Base64.decoder.decode(Files.readString(file).trim()).length == 32
        Files.getPosixFilePermissions(file) == PosixFilePermissions.fromString('rw-------')
        !Files.readString(file).contains(KEY)

        when:
        def second = new ApiKeyCipher('', file.toString())

        then:
        second.decrypt(7, 'nvidia_nim', stored) == KEY
    }

    def "a key given directly wins over the file, which is then not made"() {
        given:
        def file = tempDir.resolve('generation-master.key')

        when:
        def cipher = new ApiKeyCipher(MASTER, file.toString())

        then:
        cipher.decrypt(7, 'nvidia_nim', new ApiKeyCipher(MASTER).encrypt(7, 'nvidia_nim', KEY)) == KEY
        !Files.exists(file)
    }

    def "a key file that is wrong stops the server and is left as it is"() {
        given:
        def file = tempDir.resolve('generation-master.key')
        Files.writeString(file, 'not a key')

        when:
        new ApiKeyCipher('', file.toString())

        then:
        thrown(IllegalStateException)
        Files.readString(file) == 'not a key'
    }

    def "a file that cannot be made leaves the server without keys for teachers instead of stopping it"() {
        given:
        def blocker = tempDir.resolve('a-file-not-a-folder')
        Files.writeString(blocker, 'x')

        when:
        def cipher = new ApiKeyCipher('', blocker.resolve('generation-master.key').toString())

        then:
        !cipher.isConfigured()
    }

    def "with neither a key nor a file nothing is made"() {
        expect:
        !new ApiKeyCipher('', '').isConfigured()
    }
}

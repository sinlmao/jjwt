package io.jsonwebtoken.security

import io.jsonwebtoken.impl.security.DefaultAeadRequest
import io.jsonwebtoken.impl.security.DefaultAeadResult
import io.jsonwebtoken.impl.security.GcmAesAeadAlgorithm
import org.junit.Test

import static org.junit.Assert.*

/**
 * @since JJWT_RELEASE_VERSION
 */
class EncryptionAlgorithmsTest {

    private static final String PLAINTEXT =
            '''Bacon ipsum dolor amet venison beef pork chop, doner jowl pastrami ground round alcatra.
               Beef leberkas filet mignon ball tip pork spare ribs kevin short loin ribeye ground round
               biltong jerky short ribs corned beef. Strip steak turducken meatball porchetta beef ribs
               shoulder pork belly doner salami corned beef kielbasa cow filet mignon drumstick. Bacon
               tenderloin pancetta flank frankfurter ham kevin leberkas meatball turducken beef ribs.
               Cupim short loin short ribs shankle tenderloin. Ham ribeye hamburger flank tenderloin
               cupim t-bone, shank tri-tip venison salami sausage pancetta. Pork belly chuck salami
               alcatra sirloin.

               以ケ ホゥ婧詃 橎ちゅぬ蛣埣 禧ざしゃ蟨廩 椥䤥グ曣わ 基覧 滯っ䶧きょメ Ủ䧞以ケ妣 择禤槜谣お 姨のドゥ,
               らボみょば䪩 苯礊觊ツュ婃 䩦ディふげセ げセりょ 禤槜 Ủ䧞以ケ妣 せがみゅちょ䰯 择禤槜谣お 難ゞ滧 蝥ちゃ,
               滯っ䶧きょメ らボみょば䪩 礯みゃ楦と饥 椥䤥グ ウァ槚 訤をりゃしゑ びゃ驨も氩簥 栨キョ奎婨榞 ヌに楃 以ケ,
               姚奊べ 椥䤥グ曣わ 栨キョ奎婨榞 ちょ䰯 Ủ䧞以ケ妣 誧姨のドゥろ よ苯礊 く涥, りゅぽ槞 馣ぢゃ尦䦎ぎ
               大た䏩䰥ぐ 郎きや楺橯 䧎キェ, 難ゞ滧 栧择 谯䧟簨訧ぎょ 椥䤥グ曣わ'''

    private static final byte[] PLAINTEXT_BYTES = PLAINTEXT.getBytes("UTF-8")

    private static final String AAD = 'You can get with this, or you can get with that'
    private static final byte[] AAD_BYTES = AAD.getBytes("UTF-8")

    @Test
    void testPrivateCtor() { //for code coverage only
        new EncryptionAlgorithms()
    }

    @Test
    void testWithoutAad() {

        for (AeadAlgorithm alg : EncryptionAlgorithms.values()) {

            def key = alg.generateKey()

            def request = new DefaultAeadRequest(PLAINTEXT_BYTES, key, null)

            def result = alg.encrypt(request)

            byte[] tag = result.getDigest() //there is always a tag, even if there is no AAD
            assertNotNull tag

            byte[] ciphertext = result.getPayload()

            boolean gcm = alg instanceof GcmAesAeadAlgorithm

            if (gcm) { //AES GCM always results in ciphertext the same length as the plaintext:
                assertEquals(ciphertext.length, PLAINTEXT_BYTES.length)
            }

            def dreq = new DefaultAeadResult(null, null, ciphertext, key, null, tag, result.getInitializationVector())

            byte[] decryptedPlaintextBytes = alg.decrypt(dreq).payload

            assertArrayEquals(PLAINTEXT_BYTES, decryptedPlaintextBytes)
        }
    }

    @Test
    void testWithAad() {

        for (AeadAlgorithm alg : EncryptionAlgorithms.values()) {

            def key = alg.generateKey()

            def req = new DefaultAeadRequest(null, null, PLAINTEXT_BYTES, key, AAD_BYTES)

            def result = alg.encrypt(req)

            byte[] ciphertext = result.getPayload()

            boolean gcm = alg instanceof GcmAesAeadAlgorithm

            if (gcm) {
                assertEquals(ciphertext.length, PLAINTEXT_BYTES.length)
            }

            def dreq = new DefaultAeadResult(null, null, result.getPayload(), key, AAD_BYTES, result.getDigest(), result.getInitializationVector());
            byte[] decryptedPlaintextBytes = alg.decrypt(dreq).getPayload()
            assertArrayEquals(PLAINTEXT_BYTES, decryptedPlaintextBytes)
        }
    }
}

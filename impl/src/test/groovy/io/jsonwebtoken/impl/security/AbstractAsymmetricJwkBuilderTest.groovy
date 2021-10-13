package io.jsonwebtoken.impl.security

import io.jsonwebtoken.lang.Assert
import io.jsonwebtoken.security.Jwks
import io.jsonwebtoken.security.RsaPublicJwkBuilder
import io.jsonwebtoken.security.SignatureAlgorithms
import org.junit.Test

import java.security.cert.X509Certificate
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertSame

class AbstractAsymmetricJwkBuilderTest {

    private static final X509Certificate CERT = CertUtils.readTestCertificate(SignatureAlgorithms.RS256)
    private static final List<X509Certificate> CHAIN = [CERT]
    private static final RSAPublicKey PUB_KEY = (RSAPublicKey) CERT.getPublicKey()

    private static RsaPublicJwkBuilder builder() {
        return Jwks.builder().setKey(PUB_KEY)
    }

    @Test
    void testUse() {
        def val = UUID.randomUUID().toString()
        def jwk = builder().setPublicKeyUse(val).build()
        assertEquals val, jwk.getPublicKeyUse()
        assertEquals val, jwk.use

        def privateKey = CertUtils.readTestPrivateKey(SignatureAlgorithms.RS256)

        jwk = builder().setPublicKeyUse(val).setPrivateKey((RSAPrivateKey) privateKey).build()
        assertEquals val, jwk.getPublicKeyUse()
        assertEquals val, jwk.use
    }

    @Test
    void testX509Url() {
        def val = new URI(UUID.randomUUID().toString())
        assertSame val, builder().setX509Url(val).build().getX509Url()
    }

    @Test
    void testX509CertificateChain() {
        assertEquals CHAIN, builder().setX509CertificateChain(CHAIN).build().getX509CertificateChain()
    }

    @Test
    void testX509CertificateSha1Thumbprint() {
        def jwk = builder().setX509CertificateChain(CHAIN).withX509Sha1Thumbprint(true).build()
        Assert.notEmpty(jwk.getX509CertificateSha1Thumbprint())
        Assert.hasText(jwk.get(AbstractAsymmetricJwk.X509_SHA1_THUMBPRINT) as String)
    }

    @Test
    void testX509CertificateSha256Thumbprint() {
        def jwk = builder().setX509CertificateChain(CHAIN).withX509Sha256Thumbprint(true).build()
        Assert.notEmpty(jwk.getX509CertificateSha256Thumbprint())
        Assert.hasText(jwk.get(AbstractAsymmetricJwk.X509_SHA256_THUMBPRINT) as String)
    }
}

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.cyberark.conjur.api.Conjur;
import com.cyberark.conjur.api.Token;

public class SecretFetcher {
    public static void main(String[] args) throws Exception {
        // CLIENT LEVEL TRUST
        final String conjurTlsCaPath = "/tmp/conjur-connect/CONJUR_SSL_CERTIFICATE";

        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final FileInputStream certIs = new FileInputStream(conjurTlsCaPath);
        final Certificate cert = cf.generateCertificate(certIs);

        final KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null);
        ks.setCertificateEntry("conjurTlsCaPath", cert);

        final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext conjurSSLContext = SSLContext.getInstance("TLS");
        conjurSSLContext.init(null, tmf.getTrustManagers(), null);

        // Read token and call Conjur lib to fetch secret
        Token token = Token.fromFile(Paths.get("/run/conjur/access-token"));

        // Using custom SSLContext setup as conjurSSLContext variable
        Conjur conjur = new Conjur(token, conjurSSLContext);

        String secret0Path = System.getenv("SECRET0_PATH");
        String secret = conjur.variables().retrieveSecret(secret0Path);

        // Output the fetched secret
        System.out.println("Fetched secret: " + secret);
    }
}


package com.apns;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.interfaces.ECPrivateKey;
import org.apache.commons.codec.binary.Base64;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

public class JWT {

    private static String token;
    private static long timestamp;

    private JWT(String token, long timestamp) {
        this.token = token;
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static JWT getJWT(String keyFilePath, String teamId, String keyId) {

        String token = null;
        long nowMillis = System.currentTimeMillis();
        try {
            token = JWT.getJWT(teamId, keyId, new File(keyFilePath), nowMillis);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            return new JWT(token, nowMillis);
        }
    }

    private static String getJWT(String teamId, String keyId, File pkcs8File, long timestamp) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.ES256;
        Date now = new Date(timestamp);

        Key signingKey = signingKeyFromPkcs8File(pkcs8File);

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setIssuer(teamId)
                .setHeaderParam("kid", keyId)
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

    private static ECPrivateKey signingKeyFromPkcs8File(final File pkcs8File) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try (final FileInputStream fileInputStream = new FileInputStream(pkcs8File)) {
            return JWT.loadFromInputStream(fileInputStream);
        }
    }

    private static ECPrivateKey loadFromInputStream(final InputStream inputStream) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchAlgorithmException {

        final ECPrivateKey signingKey;
        {
            final String base64EncodedPrivateKey;
            {
                final StringBuilder privateKeyBuilder = new StringBuilder();

                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                boolean haveReadHeader = false;
                boolean haveReadFooter = false;

                for (String line; (line = reader.readLine()) != null; ) {
                    if (!haveReadHeader) {
                        if (line.contains("BEGIN PRIVATE KEY")) {
                            haveReadHeader = true;
                        }
                    } else {
                        if (line.contains("END PRIVATE KEY")) {
                            haveReadFooter = true;
                            break;
                        } else {
                            privateKeyBuilder.append(line);
                        }
                    }
                }

                if (!(haveReadHeader && haveReadFooter)) {
                    throw new IOException("Could not find private key header/footer");
                }

                base64EncodedPrivateKey = privateKeyBuilder.toString();
            }

            final byte[] keyBytes = Base64.decodeBase64(base64EncodedPrivateKey);

            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            final KeyFactory keyFactory = KeyFactory.getInstance("EC");

            try {
                signingKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                throw new InvalidKeyException(e);
            }
        }

        return signingKey;
    }
}

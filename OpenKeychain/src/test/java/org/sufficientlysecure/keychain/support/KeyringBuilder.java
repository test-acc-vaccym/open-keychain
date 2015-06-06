/*
 * Copyright (C) Art O Cathain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.keychain.support;

import org.spongycastle.bcpg.CompressionAlgorithmTags;
import org.spongycastle.bcpg.ContainedPacket;
import org.spongycastle.bcpg.HashAlgorithmTags;
import org.spongycastle.bcpg.MPInteger;
import org.spongycastle.bcpg.PublicKeyAlgorithmTags;
import org.spongycastle.bcpg.PublicKeyPacket;
import org.spongycastle.bcpg.PublicSubkeyPacket;
import org.spongycastle.bcpg.RSAPublicBCPGKey;
import org.spongycastle.bcpg.SignaturePacket;
import org.spongycastle.bcpg.SignatureSubpacket;
import org.spongycastle.bcpg.SignatureSubpacketInputStream;
import org.spongycastle.bcpg.SignatureSubpacketTags;
import org.spongycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.spongycastle.bcpg.UserIDPacket;
import org.spongycastle.bcpg.sig.Features;
import org.spongycastle.bcpg.sig.IssuerKeyID;
import org.spongycastle.bcpg.sig.KeyExpirationTime;
import org.spongycastle.bcpg.sig.KeyFlags;
import org.spongycastle.bcpg.sig.PreferredAlgorithms;
import org.spongycastle.bcpg.sig.SignatureCreationTime;
import org.spongycastle.openpgp.PGPSignature;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Helps create correct and incorrect keyrings for tests.
 *
 * The original "correct" keyring was generated by GnuPG.
 */
public class KeyringBuilder {


    private static final BigInteger PUBLIC_KEY_MODULUS = new BigInteger(
            "cbab78d90d5f2cc0c54dd3c3953005a1e6b521f1ffa5465a102648bf7b91ec72" +
            "f9c180759301587878caeb73332156209f81ca5b3b94309d96110f6972cfc56a" +
            "37fd6279f61d71f19b8f64b288e338299dce133520f5b9b4253e6f4ba31ca36a" +
            "fd87c2081b15f0b283e9350e370e181a23d31379101f17a23ae9192250db6540" +
            "2e9cab2a275bc5867563227b197c8b136c832a94325b680e144ed864fb00b9b8" +
            "b07e13f37b40d5ac27dae63cd6a470a7b40fa3c7479b5b43e634850cc680b177" +
            "8dd6b1b51856f36c3520f258f104db2f96b31a53dd74f708ccfcefccbe420a90" +
            "1c37f1f477a6a4b15f5ecbbfd93311a647bcc3f5f81c59dfe7252e3cd3be6e27"
            , 16
    );

    private static final BigInteger PUBLIC_SUBKEY_MODULUS = new BigInteger(
            "e8e2e2a33102649f19f8a07486fb076a1406ca888d72ae05d28f0ef372b5408e" +
            "45132c69f6e5cb6a79bb8aed84634196731393a82d53e0ddd42f28f92cc15850" +
            "8ce3b7ca1a9830502745aee774f86987993df984781f47c4a2910f95cf4c950c" +
            "c4c6cccdc134ad408a0c5418b5e360c9781a8434d366053ea6338b975fae88f9" +
            "383a10a90e7b2caa9ddb95708aa9d8a90246e29b04dbd6136613085c9a287315" +
            "c6e9c7ff4012defc1713875e3ff6073333a1c93d7cd75ebeaaf16b8b853d96ba" +
            "7003258779e8d2f70f1bc0bcd3ef91d7a9ccd8e225579b2d6fcae32799b0a6c0" +
            "e7305fc65dc4edc849c6130a0d669c90c193b1e746c812510f9d600a208be4a5"
            , 16
    );

    private static final Date SIGNATURE_DATE = new Date(1404566755000L);

    private static final BigInteger EXPONENT = BigInteger.valueOf(0x010001);

    private static final String USER_ID_STRING = "OpenKeychain User (NOT A REAL KEY) <openkeychain@example.com>";

    public static final BigInteger CORRECT_SIGNATURE = new BigInteger(
            "b065c071d3439d5610eb22e5b4df9e42ed78b8c94f487389e4fc98e8a75a043f" +
            "14bf57d591811e8e7db2d31967022d2ee64372829183ec51d0e20c42d7a1e519" +
            "e9fa22cd9db90f0fd7094fd093b78be2c0db62022193517404d749152c71edc6" +
            "fd48af3416038d8842608ecddebbb11c5823a4321d2029b8993cb017fa8e5ad7" +
            "8a9a618672d0217c4b34002f1a4a7625a514b6a86475e573cb87c64d7069658e" +
            "627f2617874007a28d525e0f87d93ca7b15ad10dbdf10251e542afb8f9b16cbf" +
            "7bebdb5fe7e867325a44e59cad0991cb239b1c859882e2ebb041b80e5cdc3b40" +
            "ed259a8a27d63869754c0881ccdcb50f0564fecdc6966be4a4b87a3507a9d9be"
            , 16
    );
    public static final BigInteger CORRECT_SUBKEY_SIGNATURE = new BigInteger(
            "9c40543e646cfa6d3d1863d91a4e8f1421d0616ddb3187505df75fbbb6c59dd5" +
            "3136b866f246a0320e793cb142c55c8e0e521d1e8d9ab864650f10690f5f1429" +
            "2eb8402a3b1f82c01079d12f5c57c43fce524a530e6f49f6f87d984e26db67a2" +
            "d469386dac87553c50147ebb6c2edd9248325405f737b815253beedaaba4f5c9" +
            "3acd5d07fe6522ceda1027932d849e3ec4d316422cd43ea6e506f643936ab0be" +
            "8246e546bb90d9a83613185047566864ffe894946477e939725171e0e15710b2" +
            "089f78752a9cb572f5907323f1b62f14cb07671aeb02e6d7178f185467624ec5" +
            "74e4a73c439a12edba200a4832106767366a1e6f63da0a42d593fa3914deee2b"
            , 16
    );
    public static final BigInteger KEY_ID = BigInteger.valueOf(0x15130BCF071AE6BFL);

    public static UncachedKeyRing correctRing() {
        return convertToKeyring(correctKeyringPackets());
    }

    public static UncachedKeyRing ringWithExtraIncorrectSignature() {
        List<ContainedPacket> packets = correctKeyringPackets();
        SignaturePacket incorrectSignaturePacket = createSignaturePacket(CORRECT_SIGNATURE.subtract(BigInteger.ONE));
        packets.add(2, incorrectSignaturePacket);
        return convertToKeyring(packets);
    }

    private static UncachedKeyRing convertToKeyring(List<ContainedPacket> packets) {
        try {
            return UncachedKeyRing.decodeFromData(TestDataUtil.concatAll(packets));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ContainedPacket> correctKeyringPackets() {
        PublicKeyPacket publicKey = createPgpPublicKey(PUBLIC_KEY_MODULUS);
        UserIDPacket userId = createUserId(USER_ID_STRING);
        SignaturePacket signaturePacket = createSignaturePacket(CORRECT_SIGNATURE);
        PublicKeyPacket subKey = createPgpPublicSubKey(PUBLIC_SUBKEY_MODULUS);
        SignaturePacket subKeySignaturePacket = createSubkeySignaturePacket();

        return new ArrayList<ContainedPacket>(Arrays.asList(
                publicKey,
                userId,
                signaturePacket,
                subKey,
                subKeySignaturePacket
        ));
    }

    private static SignaturePacket createSignaturePacket(BigInteger signature) {
        MPInteger[] signatureArray = new MPInteger[]{
                new MPInteger(signature)
        };

        int signatureType = PGPSignature.POSITIVE_CERTIFICATION;
        int keyAlgorithm = SignaturePacket.RSA_GENERAL;
        int hashAlgorithm = HashAlgorithmTags.SHA1;

        SignatureSubpacket[] hashedData = new SignatureSubpacket[]{
                new SignatureCreationTime(false, SIGNATURE_DATE),
                new KeyFlags(false, KeyFlags.CERTIFY_OTHER + KeyFlags.SIGN_DATA),
                new KeyExpirationTime(false, TimeUnit.DAYS.toSeconds(2)),
                new PreferredAlgorithms(SignatureSubpacketTags.PREFERRED_SYM_ALGS, false, new int[]{
                        SymmetricKeyAlgorithmTags.AES_256,
                        SymmetricKeyAlgorithmTags.AES_192,
                        SymmetricKeyAlgorithmTags.AES_128,
                        SymmetricKeyAlgorithmTags.CAST5,
                        SymmetricKeyAlgorithmTags.TRIPLE_DES
                }),
                new PreferredAlgorithms(SignatureSubpacketTags.PREFERRED_HASH_ALGS, false, new int[]{
                        HashAlgorithmTags.SHA256,
                        HashAlgorithmTags.SHA1,
                        HashAlgorithmTags.SHA384,
                        HashAlgorithmTags.SHA512,
                        HashAlgorithmTags.SHA224
                }),
                new PreferredAlgorithms(SignatureSubpacketTags.PREFERRED_COMP_ALGS, false, new int[]{
                        CompressionAlgorithmTags.ZLIB,
                        CompressionAlgorithmTags.BZIP2,
                        CompressionAlgorithmTags.ZIP
                }),
                new Features(false, Features.FEATURE_MODIFICATION_DETECTION),
                createPreferencesSignatureSubpacket()
        };
        SignatureSubpacket[] unhashedData = new SignatureSubpacket[]{
                new IssuerKeyID(false, KEY_ID.toByteArray())
        };
        byte[] fingerPrint = new BigInteger("522c", 16).toByteArray();

        return new SignaturePacket(signatureType,
                KEY_ID.longValue(),
                keyAlgorithm,
                hashAlgorithm,
                hashedData,
                unhashedData,
                fingerPrint,
                signatureArray);
    }

    /**
     * There is no Preferences subpacket in BouncyCastle, so we have
     * to create one manually.
     */
    private static SignatureSubpacket createPreferencesSignatureSubpacket() {
        SignatureSubpacket prefs;
        try {
            prefs = new SignatureSubpacketInputStream(new ByteArrayInputStream(
                    new byte[]{2, SignatureSubpacketTags.KEY_SERVER_PREFS, (byte) 0x80})
            ).readPacket();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return prefs;
    }

    private static SignaturePacket createSubkeySignaturePacket() {
        int signatureType = PGPSignature.SUBKEY_BINDING;
        int keyAlgorithm = SignaturePacket.RSA_GENERAL;
        int hashAlgorithm = HashAlgorithmTags.SHA1;

        SignatureSubpacket[] hashedData = new SignatureSubpacket[]{
                new SignatureCreationTime(false, SIGNATURE_DATE),
                new KeyFlags(false, KeyFlags.ENCRYPT_COMMS + KeyFlags.ENCRYPT_STORAGE),
                new KeyExpirationTime(false, TimeUnit.DAYS.toSeconds(2)),
        };
        SignatureSubpacket[] unhashedData = new SignatureSubpacket[]{
                new IssuerKeyID(false, KEY_ID.toByteArray())
        };
        byte[] fingerPrint = new BigInteger("234a", 16).toByteArray();
        MPInteger[] signature = new MPInteger[]{
                new MPInteger(CORRECT_SUBKEY_SIGNATURE)
        };
        return new SignaturePacket(signatureType,
                KEY_ID.longValue(),
                keyAlgorithm,
                hashAlgorithm,
                hashedData,
                unhashedData,
                fingerPrint,
                signature);
    }

    private static PublicKeyPacket createPgpPublicKey(BigInteger modulus) {
        return new PublicKeyPacket(PublicKeyAlgorithmTags.RSA_GENERAL, SIGNATURE_DATE, new RSAPublicBCPGKey(modulus, EXPONENT));
    }

    private static PublicKeyPacket createPgpPublicSubKey(BigInteger modulus) {
        return new PublicSubkeyPacket(PublicKeyAlgorithmTags.RSA_GENERAL, SIGNATURE_DATE, new RSAPublicBCPGKey(modulus, EXPONENT));
    }

    private static UserIDPacket createUserId(String userId) {
        return new UserIDPacket(userId);
    }

}
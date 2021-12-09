package com.eryansky.common.orm.mybatis.sensitive.encrypt;

import com.eryansky.common.orm.mybatis.sensitive.type.SensitiveType;
import com.eryansky.common.orm.mybatis.sensitive.type.SensitiveTypeHandler;
import com.eryansky.common.orm.mybatis.sensitive.type.SensitiveTypeRegisty;
import com.eryansky.common.orm.mybatis.sensitive.utils.Hex;
import com.eryansky.common.orm.mybatis.sensitive.IEncrypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 数据脱敏用到的AES加解密类
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2019-12-13
 */
public class AesSupport implements IEncrypt {

    protected Log log = LogFactory.getLog(this.getClass());

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String DEFAULT_PASSWORD = "ec";

    private final String password;
    private final SecretKeySpec secretKeySpec;
    private final SensitiveTypeHandler sensitiveTypeHandler = SensitiveTypeRegisty.get(SensitiveType.DEFAUL);

    public AesSupport() throws NoSuchAlgorithmException {
        this.password = DEFAULT_PASSWORD;
        this.secretKeySpec = getSecretKey(password);
    }

    public AesSupport(String password) throws NoSuchAlgorithmException {

        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password should not be null!");
        }

        this.password = password;
        this.secretKeySpec = getSecretKey(password);
    }

    @Override
    public String encrypt(String value) {

        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] content = value.getBytes(StandardCharsets.UTF_8);
            byte[] encryptData = cipher.doFinal(content);

            return Hex.bytesToHexString(encryptData);
        } catch (Exception e) {
            log.error("AES加密时出现问题，密钥为：" + sensitiveTypeHandler.handle(password));
            throw new IllegalStateException("AES加密时出现问题" + e.getMessage(), e);
        }
    }

    @Override
    public String decrypt(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        try {
            byte[] encryptData = Hex.hexStringToBytes(value);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] content = cipher.doFinal(encryptData);
            return new String(content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密时出现问题，密钥为:" + sensitiveTypeHandler.handle(password) + ",密文为：" + value);
            throw new IllegalStateException("AES解密时出现问题" + e.getMessage(), e);
        }

    }


    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        //AES 要求密钥长度为 128
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        kg.init(128, random);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        // 转换为AES专用密钥
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }
}

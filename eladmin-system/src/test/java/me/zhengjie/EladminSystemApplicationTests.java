package me.zhengjie;

import me.zhengjie.config.RsaProperties;
import me.zhengjie.utils.RsaUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EladminSystemApplicationTests {

    @Test
    public void contextLoads() {
        try {
            String s = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, "Snq1xSeVi2TkUczkdBBfOzk9vAPQOjxY+f+XbsbKXcBKWDWw+8T4nDFNUxjvUPyN2aDWSokd/VxLpWmphkSIRuArhxDhaAvLVd/YNujiHMTDnjAnI30qzBejjyWRae1tIJldWp2HRrcghrjkt5EqtEzr89220lmTlXtXKGY3qD2XOtMS5lIoysIt6cANK8dSr6GXD+VlNo8FXRyNaLNtwe7pKQM5P8wLW/thqD9MxQepyilM74cuDGfLARH3kX/7ijU9HU+4C/NeEvdc1rJPSb/hjhILeyrozuuK8xjbHxP/tJY5dslD4mcl3PTAfFjEUN2KN91PfMJVmtjCSGpihw==");
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }
}


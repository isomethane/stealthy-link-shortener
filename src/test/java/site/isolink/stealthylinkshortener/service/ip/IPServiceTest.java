package site.isolink.stealthylinkshortener.service.ip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = { "restricted-country-codes=RU,BY" })
class IPServiceTest {
    private final IPService ipService;

    @Autowired
    IPServiceTest(IPService ipService) {
        this.ipService = ipService;
    }

    @Test
    void locateIPFree() {
        assertEquals(IPLocationStatus.FREE, ipService.locateIP("154.54.40.179")); // US
        assertEquals(IPLocationStatus.FREE, ipService.locateIP("109.106.0.213")); // GE

        assertEquals(IPLocationStatus.FREE, ipService.locateIP("2e39:9c39:011e:34c2:07b8:f0d9:68b8:257d")); // FR
        assertEquals(IPLocationStatus.FREE, ipService.locateIP("f330:2f2b:93e2:eada:a3b5:33c3:e3b2:1eae")); // CH
    }

    @Test
    void locateIPRestricted() {
        assertEquals(IPLocationStatus.RESTRICTED, ipService.locateIP("2.61.92.40")); // RU
        assertEquals(IPLocationStatus.RESTRICTED, ipService.locateIP("31.148.251.114")); // BY

        assertEquals(IPLocationStatus.RESTRICTED, ipService.locateIP("2a02:6b8:a::a")); // RU
        assertEquals(IPLocationStatus.RESTRICTED, ipService.locateIP("2a0a:7d80:1:7::91:136")); // BY
    }

    @Test
    void locateIPUnknown() {
        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("0.0.0.0"));
        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("10.207.4.62"));
        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("172.16.0.1"));
        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("192.168.2.44"));
        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("255.255.255.255"));

        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("::"));
        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("fd12:3456:789a:1::1"));
        assertEquals(IPLocationStatus.UNKNOWN, ipService.locateIP("fe80::ffff:ffff:ffff:ffff"));
    }

    @Test
    void obfuscateIP() {
        assertEquals("154.54.**.***", IPServiceImpl.obfuscateIP("154.54.40.179"));
        assertEquals("109.106.*.***", IPServiceImpl.obfuscateIP("109.106.0.213"));
        assertEquals("10.207.*.**", IPServiceImpl.obfuscateIP("10.207.4.62"));
        assertEquals("172.16.*.*", IPServiceImpl.obfuscateIP("172.16.0.1"));
        assertEquals("255.255.***.***", IPServiceImpl.obfuscateIP("255.255.255.255"));

        assertEquals("2e39:9c39:011e:34c2:07b8:f0d9:****:****",
            IPServiceImpl.obfuscateIP("2e39:9c39:011e:34c2:07b8:f0d9:68b8:257d"));
        assertEquals("::", IPServiceImpl.obfuscateIP("::"));
        assertEquals("fd12:3456:789a:*::*", IPServiceImpl.obfuscateIP("fd12:3456:789a:1::1"));
        assertEquals("fe80::ffff:ffff:****:****", IPServiceImpl.obfuscateIP("fe80::ffff:ffff:ffff:ffff"));
    }
}

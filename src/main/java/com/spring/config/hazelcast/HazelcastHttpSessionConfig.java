//package com.spring.config;
//
//import com.hazelcast.config.Config;
//import com.hazelcast.config.NetworkConfig;
//import com.hazelcast.config.SerializerConfig;
//import com.hazelcast.core.Hazelcast;
//import com.hazelcast.core.HazelcastInstance;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.session.ExpiringSession;
//import org.springframework.session.MapSessionRepository;
//import org.springframework.session.SessionRepository;
//import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
//import org.springframework.util.SocketUtils;
//
//@EnableHazelcastHttpSession(maxInactiveIntervalInSeconds = 15)
////@Configuration
//public class HazelcastHttpSessionConfig {
//
//    @Bean(destroyMethod = "shutdown")
//    public HazelcastInstance hazelcastInstance() {
//        Config cfg = new Config();
//
//        NetworkConfig netConfig = new NetworkConfig();
//        netConfig.setPort(SocketUtils.findAvailableTcpPort());
//        System.out.println("Hazelcast port #: " + netConfig.getPort());
//        cfg.setNetworkConfig(netConfig);
//
//        SerializerConfig serializer = new SerializerConfig()
//                .setTypeClass(Object.class)
//                .setImplementation(new ObjectStreamSerializer());
//        cfg.getSerializationConfig().addSerializerConfig(serializer);
//
//        return Hazelcast.newHazelcastInstance(cfg);
//    }
//
//
//}

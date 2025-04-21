package com.kdu.hufflepuff.ibe.config;

import com.kdu.hufflepuff.ibe.model.enums.CacheNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(5L))
            .useSsl() // Enable TLS
            .build();

        // Use RedisStandaloneConfiguration for serverless/standalone Redis
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(host);
        standaloneConfig.setPort(redisPort);

        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        log.info("Initializing Redis cache manager with caches: {}", 
                String.join(", ", 
                    CacheNames.SPECIAL_OFFERS_CACHE,
                    CacheNames.CALENDAR_OFFERS_CACHE,
                    CacheNames.PROMO_OFFERS_CACHE,
                    CacheNames.MINIMUM_ROOM_RATES_CACHE));

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration)
            .withCacheConfiguration(CacheNames.SPECIAL_OFFERS_CACHE, cacheConfiguration)
            .withCacheConfiguration(CacheNames.CALENDAR_OFFERS_CACHE, cacheConfiguration)
            .withCacheConfiguration(CacheNames.PROMO_OFFERS_CACHE, cacheConfiguration)
            .withCacheConfiguration(CacheNames.MINIMUM_ROOM_RATES_CACHE, cacheConfiguration)
            .build();
    }
} 
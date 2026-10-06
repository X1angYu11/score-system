package com.example.academicwarning;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
@Configuration
public class CacheConfig {
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("stats",      randomTtl())
                .withCacheConfiguration("aiAdvice",   randomTtl())
                .withCacheConfiguration("aiAnalysis", randomTtl());
    }
    private RedisCacheConfiguration randomTtl(){
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl((key,value)->
                    Duration.ofSeconds(60+ThreadLocalRandom.current().nextInt(60)));
    }
}

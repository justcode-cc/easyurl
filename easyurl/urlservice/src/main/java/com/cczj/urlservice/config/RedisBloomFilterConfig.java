package com.cczj.urlservice.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

@Slf4j
@Order(1010)
@Configuration
public class RedisBloomFilterConfig {

    @Resource
    private RedissonClient redissonClient;

    private static final String BLOOM_FILTER_KEY = "easy_url_bloom_filter_url";
    // 预计插入量 设计的越大占用的内存就越多
    private static final long EXPECTED_INSERTIONS = 1_0000_0000L;
    // 误差率
    private static final double FALSE_PROBABILITY = 0.01;

    @Bean
    @Lazy
    public RBloomFilter<String> bloomFilter() {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BLOOM_FILTER_KEY);
        bloomFilter.tryInit(EXPECTED_INSERTIONS, FALSE_PROBABILITY);
        log.info("redisson bloomfilter init success");
        return bloomFilter;
    }

}

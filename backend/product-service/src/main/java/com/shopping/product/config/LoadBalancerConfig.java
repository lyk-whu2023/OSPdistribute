package com.shopping.product.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RetryAwareServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class LoadBalancerConfig {
    
    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        
        return new RoundRobinLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name
        );
    }
    
    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(Environment environment) {
        return new RetryAwareServiceInstanceListSupplier(
            new CustomServiceInstanceListSupplier(environment)
        );
    }
    
    public static class CustomServiceInstanceListSupplier implements ServiceInstanceListSupplier {
        
        private final Environment environment;
        private final String serviceId;
        
        public CustomServiceInstanceListSupplier(Environment environment) {
            this.environment = environment;
            this.serviceId = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        }
        
        @Override
        public String getServiceId() {
            return serviceId;
        }
        
        @Override
        public Flux<List<ServiceInstance>> get() {
            return (Flux<List<ServiceInstance>>) List.of();
        }
    }
}

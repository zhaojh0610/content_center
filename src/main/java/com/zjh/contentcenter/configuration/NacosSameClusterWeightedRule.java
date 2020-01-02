package com.zjh.contentcenter.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName NacosSameClusterWeightedRule
 * @Author zhaojh
 * @Date 2019/12/27 10:15
 * @Version 1.0
 * @Description //TODO
 **/
@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            //拿到配置文件中的集群名称
            String clusterName = nacosDiscoveryProperties.getClusterName();
            //拿到配置文件中的元数据
            Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
            String version = metadata.get("version");
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            //想要请求的微服务名称
            String name = loadBalancer.getName();
            //拿到服务相关的API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            //step1.找到指定服务的所有实例 A
            List<Instance> instances = namingService.selectInstances(name, true);
            //step2.过滤出相同集群下的所有实例 B
            List<Instance> sameClusterInstances = instances.stream().filter(instance -> {
                return Objects.equals(instance.getClusterName(), clusterName);
            }).collect(Collectors.toList());
            //筛选出相同集群下的相同版本号的实例
            List<Instance> sameClusterInstances2 = sameClusterInstances.stream().filter(instance -> {
                return Objects.equals(version, instance.getMetadata().get("version"));
            }).collect(Collectors.toList());
            log.info("筛选出相同集群下相同版本号的实例 = {}", sameClusterInstances);
            //筛选出不同集群下的相同版本号的实例
            List<Instance> instances2 = instances.stream().filter(instance -> {
                return Objects.equals(version, instance.getMetadata().get("version"));
            }).collect(Collectors.toList());
            log.info("筛选出不是同一个集群下相同版本号的实例={}",instances2);
            //step3.如果B为空，就用A
            List<Instance> instanceToBeChose;
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                instanceToBeChose = instances2;
                log.warn("发生了跨集群的调用，name={}，clusterName={}，instance={}", name, clusterName, instances);
            } else {
                instanceToBeChose = sameClusterInstances2;
            }
            //step4.基于权重的负载均衡算法，返回一个实例
            Instance instance = ExtendBanlancer.getHostByRandomWeight2(instanceToBeChose);
            log.info("选择的实例是 port = {},instance = {}", instance.getPort(), instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("发生了异常", e);
            return null;
        }
    }
}

class ExtendBanlancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
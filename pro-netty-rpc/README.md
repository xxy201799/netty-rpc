# pro-netty-rpc
分布式rpc架构，基于protobuf协议  
简介：  
1. zookeeper客户端采用curator框架进行节点的生成以及删除，也就是服务的发布与注册
使用zookeeper进行服务的监控发布更加安全以及方便。  
2. 使用了protobuf进行了序列化协议，该协议可以进行跨语言通信。  
3. 使用netty进行服务的真正调用，netty是一款高性能的通信框架。  

第一天：实现了简单的节点注册以及netty客户端服务端的连接。

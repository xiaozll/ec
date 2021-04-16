# ec

### 快速开始
> 项目使用技术
- Spring Boot2
> 依赖外部环境
- redis (可选)
- MariaDB/MySQL


Docker发布


    mvn package com.google.cloud.tools:jib-maven-plugin:3.0.0:build -DsendCredentialsOverHttp=true

# HMCL Region Unlock Agent

这个独立 Java Agent 针对 HMCL PR [#7017](https://github.com/HMCL-dev/HMCL/pull/7017)：
它在 `RegionUtils` 首次加载时，把 `isIsChinaMainlandNetwork()` 的返回值改为 `false`，
只解除下载页的地区限制；`isIsChinaMainlandMachine()` 保持原值，因此中国大陆的下载镜像、
Terracotta 节点和离线账户默认行为不受影响。

当前不包含 `RegionUtils` 的旧版 HMCL 也可以带着该 Agent 正常启动；Agent 会保持空闲。

## 构建

在仓库根目录执行：

```bash
mvn clean verify
```

产物位于 `target/hmcl-region-unlock-agent-1.0.1.jar`。

## 使用

把参数放在 `-jar HMCL-xxx.jar` 之前：

```bash
java -javaagent:target/hmcl-region-unlock-agent-1.0.1.jar -jar HMCL.jar
```

如果 Agent 与 HMCL jar 放在同一个目录，可以把路径缩短为：

```bash
java -javaagent:hmcl-region-unlock-agent-1.0.1.jar -jar HMCL.jar
```

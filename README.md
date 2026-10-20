# HMCL Region Unlock Agent

这个独立 Java Agent 针对 HMCL PR [#7017](https://github.com/HMCL-dev/HMCL/pull/7017)：
它在 `RegionUtils` 首次加载时，把 `isIsChinaMainlandMachine()` 和
`isIsChinaMainlandNetwork()` 的返回值改为 `false`。

当前不包含 `RegionUtils` 的旧版 HMCL 也可以带着该 Agent 正常启动；Agent 会保持空闲。

## 构建

在仓库根目录执行：

```bash
./gradlew -p region-unlock-agent clean check jar
```

产物位于 `region-unlock-agent/build/libs/hmcl-region-unlock-agent-1.0.0.jar`。

## 使用

把参数放在 `-jar HMCL-xxx.jar` 之前：

```bash
java -javaagent:region-unlock-agent/build/libs/hmcl-region-unlock-agent-1.0.0.jar -jar HMCL.jar
```

如果 Agent 与 HMCL jar 放在同一个目录，可以把路径缩短为：

```bash
java -javaagent:hmcl-region-unlock-agent-1.0.0.jar -jar HMCL.jar
```

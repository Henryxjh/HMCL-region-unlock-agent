package io.github.henryxjh.hmclregionunlock;

import org.jackhuang.hmcl.util.RegionUtils;

public final class AgentIntegrationTest {
    public static void main(String[] args) {
        if (!RegionUtils.isIsChinaMainlandMachine()) {
            throw new AssertionError("The agent must preserve machine region detection");
        }
        if (RegionUtils.isIsChinaMainlandNetwork()) {
            throw new AssertionError("The agent did not disable the network restriction");
        }
        System.out.println("HMCL region unlock agent integration test passed");
    }
}

/*
 * A small compatibility agent for HMCL PR #7017.
 *
 * This project is deliberately independent from HMCL so that it can be built
 * before the proposed RegionUtils class lands in the main branch.
 */
package io.github.henryxjh.hmclregionunlock;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public final class RegionUnlockAgent {
    static final String TARGET_CLASS = "org/jackhuang/hmcl/util/RegionUtils";

    private RegionUnlockAgent() {
    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        instrumentation.addTransformer(new RegionTransformer());
    }

    static final class RegionTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer) {
            if (!TARGET_CLASS.equals(className)) {
                return null;
            }

            try {
                return disableMainlandDetection(classfileBuffer);
            } catch (Throwable throwable) {
                System.err.println("[HMCL Region Unlock Agent] Failed to patch RegionUtils; leaving it unchanged.");
                throwable.printStackTrace(System.err);
                return null;
            }
        }
    }

    static byte[] disableMainlandDetection(byte[] original) {
        ClassReader reader = new ClassReader(original);
        ClassWriter writer = new ClassWriter(reader, 0);

        reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions) {
                MethodVisitor delegate = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (("isIsChinaMainlandMachine".equals(name)
                        || "isIsChinaMainlandNetwork".equals(name))
                        && "()Z".equals(descriptor)) {
                    return new AlwaysFalseMethodVisitor(delegate);
                }
                return delegate;
            }
        }, 0);

        return writer.toByteArray();
    }

    private static final class AlwaysFalseMethodVisitor extends MethodVisitor {
        private boolean emitted;

        private AlwaysFalseMethodVisitor(MethodVisitor delegate) {
            super(Opcodes.ASM9, delegate);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            super.visitInsn(Opcodes.ICONST_0);
            super.visitInsn(Opcodes.IRETURN);
            emitted = true;
        }

        @Override public void visitInsn(int opcode) { }
        @Override public void visitIntInsn(int opcode, int operand) { }
        @Override public void visitVarInsn(int opcode, int varIndex) { }
        @Override public void visitTypeInsn(int opcode, String type) { }
        @Override public void visitFieldInsn(int opcode, String owner, String name, String descriptor) { }
        @Override public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) { }
        @Override public void visitInvokeDynamicInsn(String name, String descriptor, org.objectweb.asm.Handle handle, Object... arguments) { }
        @Override public void visitJumpInsn(int opcode, org.objectweb.asm.Label label) { }
        @Override public void visitLdcInsn(Object value) { }
        @Override public void visitIincInsn(int varIndex, int increment) { }
        @Override public void visitTableSwitchInsn(int min, int max, org.objectweb.asm.Label defaultLabel, org.objectweb.asm.Label... labels) { }
        @Override public void visitLookupSwitchInsn(org.objectweb.asm.Label defaultLabel, int[] keys, org.objectweb.asm.Label[] labels) { }
        @Override public void visitMultiANewArrayInsn(String descriptor, int dimensions) { }
        @Override public void visitTryCatchBlock(org.objectweb.asm.Label start, org.objectweb.asm.Label end, org.objectweb.asm.Label handler, String type) { }
        @Override public void visitLabel(org.objectweb.asm.Label label) { }
        @Override public void visitLineNumber(int line, org.objectweb.asm.Label start) { }
        @Override public void visitLocalVariable(String name, String descriptor, String signature, org.objectweb.asm.Label start, org.objectweb.asm.Label end, int index) { }
        @Override public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) { }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            if (!emitted) {
                throw new IllegalStateException("Target method has no Code attribute");
            }
            super.visitMaxs(1, maxLocals);
        }
    }
}

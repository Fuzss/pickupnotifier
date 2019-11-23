package com.fuzs.pickupnotifier.asm.core;

import com.fuzs.pickupnotifier.PickUpNotifier;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ClassTransformerUtil {

    public static byte[] transformBasicClass(byte[] basicClass, MethodSignature signature, Predicate<AbstractInsnNode> filter, BiConsumer<MethodNode, AbstractInsnNode> insert) {

        String name = signature.getClassName();
        log("Patching " + name + "...");

        ClassReader classReader;
        try {
            classReader = new ClassReader(basicClass);
        } catch (NullPointerException ignored) {
            return basicClass;
        }

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        Optional<MethodNode> method = findMethod(classNode, signature);
        if (method.isPresent()) {

            Optional<AbstractInsnNode> node = findNode(method.get(), filter);
            if (node.isPresent()) {

                insert.accept(method.get(), node.get());
                log("Patching " + name + " was successful");
                ClassWriter classWriter = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                classNode.accept(classWriter);
                return classWriter.toByteArray();

            } else {
                log("Patching " + name + " failed: " + "Node not found");
            }
        } else {
            log("Patching " + name + " failed: " + "Method not found");
        }

        return basicClass;

    }

    private static Optional<MethodNode> findMethod(ClassNode classNode, MethodSignature signature) {

        for (MethodNode method : classNode.methods) {
            if (signature.matches(method)) {
                return Optional.of(method);
            }
        }

        return Optional.empty();

    }

    private static Optional<AbstractInsnNode> findNode(MethodNode method, Predicate<AbstractInsnNode> filter) {

        for (AbstractInsnNode node : method.instructions.toArray()) {
            if (filter.test(node)) {
                return Optional.of(node);
            }
        }

        return Optional.empty();

    }

    public static AbstractInsnNode getNthNode(AbstractInsnNode node, int n) {

        for (int i = 0; i < Math.abs(n); i++) {
            if (n < 0) {
                node = node.getPrevious();
            } else {
                node = node.getNext();
            }
        }

        return node;

    }

    private static void log(String s) {
        LogManager.getLogger(PickUpNotifier.NAME + " Transformer").info(s);
    }

}

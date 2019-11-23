package com.fuzs.pickupnotifier.asm;

import com.fuzs.pickupnotifier.asm.core.ClassTransformerUtil;
import com.fuzs.pickupnotifier.asm.core.MethodSignature;
import com.fuzs.pickupnotifier.asm.hook.AddEntriesHook;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ClassTransformer implements IClassTransformer, Opcodes {

    private static final Map<String, Function<byte[], byte[]>> TRANSFORMABLES = new HashMap<String, Function<byte[], byte[]>>(){{
        put("net.minecraft.client.network.NetHandlerPlayClient", ClassTransformer::transformNetHandlerPlayClient);
    }};

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (TRANSFORMABLES.containsKey(transformedName)) {
            return TRANSFORMABLES.get(transformedName).apply(basicClass);
        }

        return basicClass;

    }

    private static byte[] transformNetHandlerPlayClient(byte[] basicClass) {

        MethodSignature signature = new MethodSignature("net/minecraft/client/network/NetHandlerPlayClient", "handleCollectItem", "func_147246_a", "(Lnet/minecraft/network/play/server/SPacketCollectItem;)V");
        MethodSignature hook = new MethodSignature(Type.getInternalName(AddEntriesHook.class), "onEntityPickup", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityLivingBase;)V");
        MethodSignature getAmount = new MethodSignature("net/minecraft/network/play/server/SPacketCollectItem", "getAmount", "func_191208_c", "()I");
        MethodSignature setCount = new MethodSignature("net/minecraft/item/ItemStack", "setCount", "func_190920_e", "(I)V");

        Predicate<AbstractInsnNode> filter = (AbstractInsnNode node) -> {
            if (node.getOpcode() == INVOKEVIRTUAL && node instanceof MethodInsnNode && getAmount.matches((MethodInsnNode) node)) {
                node = ClassTransformerUtil.getNthNode(node, 1);
                return node.getOpcode() == INVOKEVIRTUAL && node instanceof MethodInsnNode && setCount.matches((MethodInsnNode) node);
            }
            return false;
        };

        BiConsumer<MethodNode, AbstractInsnNode> insert = (MethodNode method, AbstractInsnNode node) -> {
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(ALOAD, 2));
            insnList.add(new VarInsnNode(ALOAD, 3));
            insnList.add(hook.genInsnNode());
            method.instructions.insertBefore(ClassTransformerUtil.getNthNode(node, 4), insnList);
        };

        return ClassTransformerUtil.transformBasicClass(basicClass, signature, filter, insert);

    }

}

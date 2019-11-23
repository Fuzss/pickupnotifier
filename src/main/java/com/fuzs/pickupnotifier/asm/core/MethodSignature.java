package com.fuzs.pickupnotifier.asm.core;

import com.fuzs.pickupnotifier.asm.LoadingPlugin;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

@SuppressWarnings("WeakerAccess")
public class MethodSignature {

    private final String owner, name, obfName, desc;

    public MethodSignature(String owner, String name, String obfName, String desc) {
        this.owner = owner;
        this.name = name;
        this.obfName = obfName;
        this.desc = desc;
    }

    public MethodSignature(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.obfName = name;
        this.desc = desc;
    }

    public MethodInsnNode genInsnNode() {
        return new MethodInsnNode(INVOKESTATIC, this.owner, this.name, this.desc, false);
    }

    public boolean matches(MethodNode method) {
        return (LoadingPlugin.runtimeDeobfuscationEnabled ? method.name.equals(this.obfName) : method.name.equals(this.name))
                && method.desc.equals(this.desc);
    }

    public boolean matches(MethodInsnNode method) {
        return (LoadingPlugin.runtimeDeobfuscationEnabled ? method.name.equals(this.obfName) : method.name.equals(this.name))
                && method.desc.equals(this.desc) && method.owner.equals(this.owner);
    }

    public String getClassName() {
        return this.owner.replaceAll(".*/", "");
    }

}

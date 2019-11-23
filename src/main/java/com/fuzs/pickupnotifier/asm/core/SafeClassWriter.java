package com.fuzs.pickupnotifier.asm.core;

import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassWriter;

/**
 * Safe class writer
 * The way COMPUTE_FRAMES works may require loading additional classes. This can cause ClassCircularityErrors
 * The override for getCommonSuperClass will ensure that COMPUTE_FRAMES works properly by using the right ClassLoader
 * Taken from Clothesline mod for Minecraft by jamieswhiteshirt_ (https://github.com/JamiesWhiteShirt/clothesline)
 * Found via Quark mod for Minecraft by Vazkii (https://github.com/Vazkii/Quark)
 */
public class SafeClassWriter extends ClassWriter {

    public SafeClassWriter(int flags) {
        super(flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {

        Class<?> c, d;
        ClassLoader classLoader = Launch.classLoader;
        try {
            c = Class.forName(type1.replace('/', '.'), false, classLoader);
            d = Class.forName(type2.replace('/', '.'), false, classLoader);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }

    }

}
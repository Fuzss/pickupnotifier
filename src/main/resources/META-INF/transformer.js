var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');

function initializeCoreMod() {

    return {

        'item_pickup_particle_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.particle.ItemPickupParticle'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "<init>",
                    name: "<init>",
                    desc: "(Lnet/minecraft/client/renderer/entity/EntityRendererManager;Lnet/minecraft/client/renderer/RenderTypeBuffers;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)V",
                    patches: [patchItemPickupParticleConstructor]
                }], classNode, "ItemPickupParticle");
                return classNode;
            }
        }
    };
}

function patchMethod(entries, classNode, name) {

    log("Patching " + name + "...");
    for (var i = 0; i < entries.length; i++) {

        var entry = entries[i];
        var method = findMethod(classNode.methods, entry);
        var flag = !!method;
        if (flag) {

            var obfuscated = !method.name.equals(entry.name);
            for (var j = 0; j < entry.patches.length; j++) {

                var patch = entry.patches[j];
                if (!patchInstructions(method, patch.filter, patch.action, obfuscated)) {

                    flag = false;
                }
            }
        }

        log("Patching " + name + "#" + entry.name + (flag ? " was successful" : " failed"));
    }
}

function findMethod(methods, entry) {

    for (var i = 0; i < methods.length; i++) {

        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {

            return method;
        }
    }
}

function patchInstructions(method, filter, action, obfuscated) {

    var instructions = method.instructions.toArray();
    for (var i = 0; i < instructions.length; i++) {

        var node = filter(instructions[i], obfuscated);
        if (!!node) {

            break;
        }
    }

    if (!!node) {

        action(node, method.instructions, obfuscated);
        return true;
    }
}

var patchItemPickupParticleConstructor = {
    filter: function(node, obfuscated) {
        if (node instanceof InsnNode && node.getOpcode().equals(Opcodes.RETURN)) {
            return  node;
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 4));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 5));
        insnList.add(generateHook("onEntityPickup", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)V"));
        instructions.insertBefore(node, insnList);
    }
};

function matchesMethod(node, owner, name, desc) {

    return node instanceof MethodInsnNode && matchesNode(node, owner, name, desc);
}

function matchesField(node, owner, name, desc) {

    return node instanceof FieldInsnNode && matchesNode(node, owner, name, desc);
}

function matchesNode(node, owner, name, desc) {

    return node.owner.equals(owner) && node.name.equals(name) && node.desc.equals(desc);
}

function generateHook(name, desc) {

    return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/pickupnotifier/asm/AddEntriesHandler", name, desc, false);
}

function getNthNode(node, n) {

    for (var i = 0; i < Math.abs(n); i++) {

        if (n < 0) {

            node = node.getPrevious();
        } else {

            node = node.getNext();
        }
    }

    return node;
}

function log(message) {

    print("[Pick Up Notifier Transformer]: " + message);
}
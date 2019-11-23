var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');

function initializeCoreMod() {
    return {
        'client_play_net_handler_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.network.play.ClientPlayNetHandler'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_147246_a",
                    name: "handleCollectItem",
                    desc: "(Lnet/minecraft/network/play/server/SCollectItemPacket;)V",
                    patch: patchClientPlayNetHandlerHandleCollectItem
                }, classNode, "ClientPlayNetHandler");
                return classNode;
            }
        }
    };
}

function findMethod(methods, entry) {
    var length = methods.length;
    for (var i = 0; i < length; i++) {
        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {
            return method;
        }
    }
    return null;
}

function patch(entry, classNode, name) {
    var method = findMethod(classNode.methods, entry);
    var flag;
    log("Patching " + name + "...");
    if (method !== null) {
        var obfuscated = method.name.equals(entry.obfName);
        flag = entry.patch(method, obfuscated);
    }
    if (flag) {
        log("Patching " + name + " was successful");
    } else {
        log("Patching " + name + " failed");
    }
}

function patchClientPlayNetHandlerHandleCollectItem(method, obfuscated) {
    var getAmount = obfuscated ? "func_191208_c" : "getAmount";
    var setCount = obfuscated ? "func_190920_e" : "setCount";
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof MethodInsnNode && node.getOpcode().equals(Opcodes.INVOKEVIRTUAL) && node.owner.equals("net/minecraft/network/play/server/SCollectItemPacket") && node.name.equals(getAmount) && node.desc.equals("()I")) {
            var nextNode = node.getNext();
            if (nextNode instanceof MethodInsnNode && nextNode.getOpcode().equals(Opcodes.INVOKEVIRTUAL) && nextNode.owner.equals("net/minecraft/item/ItemStack") && nextNode.name.equals(setCount) && nextNode.desc.equals("(I)V")) {
                foundNode = node;
                break;
            }
        }
    }
    if (foundNode != null) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 3));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/pickupnotifier/handler/AddEntriesHandler", "onEntityPickup", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/LivingEntity;)V", false));
        method.instructions.insertBefore(getNthNode(foundNode, 4), insnList);
        return true;
    }
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

function log(s) {
    print("[Pick Up Notifier Transformer]: " + s);
}